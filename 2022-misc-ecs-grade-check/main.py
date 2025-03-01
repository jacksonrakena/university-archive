from random import seed
from random import random
from typing import Dict, List

import mechanicalsoup
from bs4 import BeautifulSoup
from tinydb import TinyDB, Query
import time
from datetime import datetime, timedelta
import log
import config
import notify

class ECSResult:
    def __init__(self, subject: str, assig: str, mark: str):
        self.subject = subject
        self.assig = assig
        self.mark = mark

    def email_format_space(self, minlen: int) -> str:
        """
        Format with spaces, padding between : and mark to reach minlen
        :param minlen: the minimum length
        :return:
        """
        diff = minlen - len(self.email_format())
        spaces = " " * diff
        return str(self.assig) + ": " + spaces + str(self.mark)

    def email_format(self) -> str:
        """
        Format for email
        :return:
        """
        return str(self.assig) + ": " + str(self.mark)

    def __str__(self):
        return str(self.subject) + ": " + str(self.assig) + ": " + str(self.mark) + "\n"

    def __repr__(self):
        return str(self)

def format_results(results: Dict[str, List[ECSResult]]) -> str:
    """
    Format a list of results
    :param results:
    :return:
    """
    str_list = []
    first = True
    for subject in results.keys():
        if first:
            first = False
        else:
            str_list.append("\n")
        str_list.append("=" * len(subject) + "\n")
        str_list.append(subject + "\n")
        str_list.append("=" * len(subject) + "\n")
        maxlen = 0
        # Find the max size to align mark with
        for result in results.get(subject):
            currlen = len(result.email_format())
            if currlen > maxlen:
                maxlen = currlen

        # Append to string list
        for result in results.get(subject):
            # str_list.append(" - " + result.assig + ": " + str(result.mark) + "\n")
            str_list.append(result.email_format_space(maxlen) + "\n")

    return "".join(str_list)


def notify_new_results(new_results: Dict[str, List[ECSResult]]):
    """
    Emails the new results
    :param new_results: the new results
    """
    print(f"Notifying {len(new_results)} subjects with results")
    notify.notify(new_results)

def query(db: TinyDB, epoch: int):
    """
    Query and send email
    :param db: the database
    :param epoch: the number of successful tries since the program was started
    """
    browser = mechanicalsoup.StatefulBrowser()
    browser.open("https://apps.ecs.vuw.ac.nz/cgi-bin/studentmarks")
    browser.select_form("form[action=\"/login-ticket\"]")

    browser["username"] = config.get_username()
    browser["password"] = config.get_password()
    browser.submit_selected()

    # Full list of results
    results = []
    # Dict of new results <str course, Result result>
    new_results = {}

    page = str(browser.page)
    # page = testing.defaultPage
    soup = BeautifulSoup(page, "html.parser")
    # print(list(soup.children))
    # subject_tags = soup.find_all("h3")
    # for subject_tag in subject_tags:
    #     subject_str = subject_tag.text.strip()
    #     subject_map[subject_str] = []

    mark_tags = soup.find_all(lambda tag: tag.name == "span" and "Final Mark" in tag.text)
    for mark in mark_tags:
        assig_tag = mark.find_parent("h4").find(text=True, recursive=False)
        assig_str = assig_tag.strip().replace("_", " ")
        subject_str = mark.find_previous("h3").text.strip()

        s = assig_str + ": " + mark.text.strip()[len("Final Mark: "):]
        mark = mark.text.strip()[len("Final Mark: "):]
        x = ECSResult(subject_str, assig_str, mark)
        results.append(x)

    # Check for each result, whether the database already contains it
    for result in results:
        entry_query = Query()
        db_result = db.search(
            (entry_query.subject == result.subject) & (entry_query.assig == result.assig) & (
                    entry_query.mark == result.mark))
        if (len(db_result)) == 0:
            db.insert({"subject": result.subject, "assig": result.assig, "mark": result.mark})
            if result.subject not in new_results:
                new_results[result.subject] = []
            new_results.get(result.subject).append(result)

    time_str = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    if epoch > 0:
        if len(new_results) > 0:
            notify_new_results(new_results)
            log.print_log("Email sent at: " + time_str)
            log.log("=================NEW RESULTS=================")
            log.log(format_results(new_results))
            log.log("=============================================")
        else:
            log.print_log("No new results at: " + time_str)
    else:
        log.print_log("Initialized " + str(len(results)) + " results at: " + time_str)
        log.print_log("===============INITIAL RESULTS===============")
        log.print_log(format_results(new_results))
        log.print_log("=============================================")

    # pprint.pprint(subject_map)


def wait_on_exception():
    """
    Wait a few minutes when there is an exception
    :return:
    """
    sleep_seconds = 60 + (random() * (180 - 60))
    log.print_log("Waiting for " + str(int(sleep_seconds)) + " seconds to retry, until")
    time.sleep(sleep_seconds)


def main():
    db = TinyDB("db.json")
    # Drop tables
    db.drop_tables()

    # Clear the log
    log.clear()
    seed()
    epoch = 0
    while True:
        if config.within_active_hours():
            try:
                query(db, epoch)
            except ConnectionError:
                log.print_log("ConnectionError Handled")
                wait_on_exception()
            except:
                log.print_log("Other Error Handled")
                wait_on_exception()
            else:
                epoch += 1
                # Sleep
                sleep_minutes = 15 + (random() * (30 - 15))
                log.log_sleep(sleep_minutes)
                time.sleep(sleep_minutes * 60)
        else:
            sleep_seconds = config.seconds_till_active_hours_begin()
            log.log_sleep(sleep_seconds / 60.0)
            time.sleep(sleep_seconds)


if __name__ == "__main__":
    main()

