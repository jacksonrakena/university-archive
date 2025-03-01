using System;
using System.Reflection;
using SchoolBuddy.Models;
using SchoolBuddy.ViewModels;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace SchoolBuddy
{
    public sealed partial class SettingsPage : Page
    {
        private readonly SettingsPageViewModel ViewModel = new SettingsPageViewModel();
        private string Version => Assembly.GetEntryAssembly().GetName().Version.ToString();

        public SettingsPage()
        {
            InitializeComponent();
        }

        private void SettingsPage_Unloaded(object sender, RoutedEventArgs e)
        {
            ViewModel.PropertyChanged -= CheckNceaLevelDisplayed;
        }

        private void SettingsPage_Loaded(object sender, RoutedEventArgs e)
        {
            CheckNceaLevelDisplayed(null, null);
            ViewModel.PropertyChanged += CheckNceaLevelDisplayed;
        }

        private void CheckNceaLevelDisplayed(object sender, System.ComponentModel.PropertyChangedEventArgs e)
        {
            if (ViewModel.CurriculumPathway == CurriculumPathway.NationalCertificate)
            {
                NceaLevelStackPanel.Visibility = Visibility.Visible;
            }
            else
            {
                NceaLevelStackPanel.Visibility = Visibility.Collapsed;
            }
        }
        private SettingsNotifyPropertyChanged<string, NceaLevel> NceaLevel => Settings.NceaLevel;


        private void Setting_DueSoon_Click(object sender, RoutedEventArgs e)
        {
            MenuFlyoutItem mfi = (MenuFlyoutItem) sender;
            TimeSpan timeSpan = TimeSpan.FromDays(int.Parse(mfi.Tag.ToString()));
            ViewModel.DueSoonTime = timeSpan;
        }

        private void Setting_Pathway_Click(object sender, RoutedEventArgs e)
        {
            MenuFlyoutItem mfi = (MenuFlyoutItem) sender;
            ViewModel.CurriculumPathway = Enum.Parse<CurriculumPathway>(mfi.Name);
        }

        private void Setting_NceaLevel_Click(object sender, RoutedEventArgs e)
        {
            MenuFlyoutItem mfi = (MenuFlyoutItem) sender;
            Settings.NceaLevel.StoreValue = Enum.Parse<NceaLevel>(mfi.Name);
        }

        private async void Avatar_Change_Click(object sender, RoutedEventArgs e)
        {
            Windows.Storage.Pickers.FileOpenPicker picker = new Windows.Storage.Pickers.FileOpenPicker
            {
                ViewMode = Windows.Storage.Pickers.PickerViewMode.Thumbnail,
                SuggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.PicturesLibrary
            };
            picker.FileTypeFilter.Add(".jpg");
            picker.FileTypeFilter.Add(".jpeg");
            picker.FileTypeFilter.Add(".png");

            Windows.Storage.StorageFile file = await picker.PickSingleFileAsync();
            if (file != null)
            {
                Windows.Storage.StorageFolder folder = await Data.GetFolder().CreateFolderAsync("avatar", Windows.Storage.CreationCollisionOption.OpenIfExists);
                System.Collections.Generic.IReadOnlyList<Windows.Storage.StorageFile> files = await folder.GetFilesAsync();
                foreach (Windows.Storage.StorageFile file1 in files)
                {
                    await file1.DeleteAsync(Windows.Storage.StorageDeleteOption.PermanentDelete);
                }

                Windows.Storage.StorageFile newFile = await file.CopyAsync(folder, file.Name, Windows.Storage.NameCollisionOption.ReplaceExisting);
                ViewModel.AvatarPath = newFile.Path;
            }
        }

        private async void ResetData(object sender, RoutedEventArgs a)
        {
            await ResetDataConfirmation.ShowAsync();
        }

        private async void ResetDataConfirmation_PrimaryButtonClick(ContentDialog sender, ContentDialogButtonClickEventArgs args)
        {
            await Data.ResetAsync();
            Application.Current.Exit();
        }
    }
}
