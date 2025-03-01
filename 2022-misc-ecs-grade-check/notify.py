from typing import Dict, List

from discord_webhook import DiscordWebhook, DiscordEmbed

import config
from main import ECSResult

def notify(new_results: Dict[str, List[ECSResult]]):
    if len(new_results) == 0:
        return
    print("Notifying " + len(new_results).__str__())
    embeds=[]
    for subject in new_results.keys():
        for result in new_results[subject]:
            embed = DiscordEmbed()
            embed.set_title("New result released: " + result.assig)
            embed.set_description("Subject: " + result.subject)
            embeds.append(embed)

    webhook = DiscordWebhook(
        url=config.get_webhook_url())
    webhook.embeds = embeds
    webhook.execute()
