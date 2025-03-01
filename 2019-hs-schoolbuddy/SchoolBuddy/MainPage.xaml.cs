using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Media.Animation;
using Windows.UI.Xaml.Navigation;

namespace SchoolBuddy
{
    public sealed partial class MainPage : Page
    {
        public static MainPage Instance;

        public const bool EnableShowAssignmentsLoadedTip = false;

        public MainPage()
        {
            Instance = this;
            InitializeComponent();
        }

        private readonly List<(string Tag, Type Page)> _pages = new List<(string Tag, Type Page)>
        {
            ("home", typeof(HomePage)),
            ("timetable", typeof(TimetablePage)),
            ("assignments", typeof(AssignmentsPage)),
            ("dailynotices", typeof(DailyNotices))
        };

        public void NavView_Navigate(string targetTag, NavigationTransitionInfo transitionInfo)
        {
            Type page = null;
            if (targetTag == "settings")
            {
                page = typeof(SettingsPage);
            }
            else
            {
                (string Tag, Type Page) item = _pages.FirstOrDefault(p => p.Tag == targetTag);
                if (item.Page == null)
                {
                    Debug.WriteLine("Could not find page with tag \"" + targetTag + "\"");
                    return;
                }
                page = item.Page;
            }
            Type preNavPageType = MainAppFrame.CurrentSourcePageType;

            if (page != null && preNavPageType != page)
            {
                MainAppFrame.Navigate(page, null, transitionInfo);
            }
        }

        private void NavView_Loaded(object sender, RoutedEventArgs e)
        {
            MainAppFrame.Navigated += On_Navigated;
            MainAppNavigationView.SelectedItem = MainAppNavigationView.MenuItems[0];
            NavView_Navigate("home", new EntranceNavigationTransitionInfo());
        }

        private void NavView_ItemInvoked(NavigationView sender,
                                 NavigationViewItemInvokedEventArgs args)
        {
            if (args.IsSettingsInvoked)
            {
                NavView_Navigate("settings", args.RecommendedNavigationTransitionInfo);
            }
            else if (args.InvokedItemContainer != null)
            {
                string navItemTag = args.InvokedItemContainer.Tag.ToString();
                NavView_Navigate(navItemTag, args.RecommendedNavigationTransitionInfo);
            }
        }

        private void On_Navigated(object sender, NavigationEventArgs e)
        {
            NavigationView NavView = MainAppNavigationView;
            Frame ContentFrame = MainAppFrame;

            NavView.IsBackEnabled = ContentFrame.CanGoBack;

            if (ContentFrame.SourcePageType == typeof(SettingsPage))
            {
                NavView.SelectedItem = (NavigationViewItem) NavView.SettingsItem;
            }
            else if (ContentFrame.SourcePageType != null)
            {
                (string Tag, Type Page) item = _pages.FirstOrDefault(p => p.Page == e.SourcePageType);

                NavView.SelectedItem = NavView.MenuItems
                    .OfType<NavigationViewItem>()
                    .First(n => n.Tag.Equals(item.Tag));
            }
        }
    }
}
