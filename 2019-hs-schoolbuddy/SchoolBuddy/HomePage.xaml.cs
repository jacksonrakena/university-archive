using System;
using System.Collections.ObjectModel;
using SchoolBuddy.Models;
using SchoolBuddy.ViewModels;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Media.Animation;

namespace SchoolBuddy
{
    public sealed partial class HomePage : Page
    {
        public HomePageViewModel ViewModel = new HomePageViewModel();

        public ObservableCollection<QuickAction> QuickActions = new ObservableCollection<QuickAction>
        {
            new QuickAction
            {
                Name = "View timetable",
                Description = "View and update current schedule",
                PageId = "timetable"
            }
        };

        private static int FetchAssignmentCount()
        {
            TimeSpan dds = Settings.DueSoonTime;
            DateTime now = DateTime.Now;
            int count = 0;
            foreach (Assignment assignment in Assignment.Assignments)
            {
                DateTimeOffset due = assignment.DueDate;
                TimeSpan diff = due - now;
                if (diff.TotalSeconds < 0 || diff.TotalSeconds > dds.TotalSeconds)
                {
                    continue;
                }

                count++;
            }
            return count;
        }

        private string GenerateIds()
        {
            string nsn = Settings.NationalStudentNumber;
            string studentid = Settings.StudentId;

            return $"Student ID: {studentid} | National Student Number: {nsn}";
        }

        public HomePage()
        {
            InitializeComponent();
            int c = FetchAssignmentCount();
            QuickActions.Add(new QuickAction
            {
                Name = "Assignments",
                Description = c == 0 ? "You're all caught up" : $"{c} assignment{(c == 1 ? " is" : "s are")} due soon",
                PageId = "assignments"
            });
            if (Settings.CurriculumPathway == CurriculumPathway.NationalCertificate)
            {
                QuickActions.Add(new QuickAction
                {
                    Name = "National Certificate",
                    Description = "104 NCEA credits gained so far" + Environment.NewLine + "56 Excellence Endorsed" + Environment.NewLine + "0 Merit Endorsed",
                    PageId = "ncea"
                });
            }
            else
            {
                QuickActions.Add(new QuickAction
                {
                    Name = "IB Diploma",
                    Description = "24% completed" + Environment.NewLine + "Extended Essay not complete",
                    PageId = "ibdp"
                });
            }
        }

        private string GetTime()
        {
            DateTime time = DateTime.Now;
            TimeZoneInfo tz = TimeZoneInfo.Local;
            return "Local time: " + time.ToString("F") + " - " + tz.DisplayName;
        }

        private void GridView_ItemClick(object sender, ItemClickEventArgs e)
        {
            QuickAction action = (QuickAction) e.ClickedItem;
            MainPage.Instance.NavView_Navigate(action.PageId, new EntranceNavigationTransitionInfo());
        }

        private void WelcomeUsername_Loaded(object sender, Windows.UI.Xaml.RoutedEventArgs e)
        {
            if (WelcomeUsername.Text == "No name set")
            {
                ConfigureTip.IsOpen = true;
            }
        }

        private void Hyperlink_Click(Windows.UI.Xaml.Documents.Hyperlink sender, Windows.UI.Xaml.Documents.HyperlinkClickEventArgs args)
        {
            MainPage.Instance.NavView_Navigate("settings", new EntranceNavigationTransitionInfo());
            ConfigureTip.IsOpen = false;
        }
    }
}
