using System;
using System.Threading.Tasks;
using Windows.Storage;

namespace SchoolBuddy.Models
{
    public static class Data
    {
        public static ApplicationDataContainer GetRootContainer()
        {
            return ApplicationData.Current.RoamingSettings;
        }

        public static ApplicationDataContainer GetContainer(string name)
        {
            return ApplicationData.Current.RoamingSettings.CreateContainer(name, ApplicationDataCreateDisposition.Always);
        }

        public static StorageFolder GetFolder()
        {
            return ApplicationData.Current.RoamingFolder;
        }

        public static async Task ResetAsync()
        {
            ApplicationDataContainer root = GetRootContainer();
            root.DeleteContainer("settings");
            root.DeleteContainer("assignments");
            StorageFolder folder = GetFolder();
            System.Collections.Generic.IReadOnlyList<StorageFile> files = await folder.GetFilesAsync();
            foreach (StorageFile file in files)
            {
                try
                {
                    await file.DeleteAsync(StorageDeleteOption.PermanentDelete);
                }
                finally
                {

                }
            }
            System.Collections.Generic.IReadOnlyList<StorageFile> avatarFiles = await (await folder.GetFolderAsync("avatar")).GetFilesAsync();
            foreach (StorageFile file in avatarFiles)
            {
                try
                {
                    await file.DeleteAsync(StorageDeleteOption.PermanentDelete);
                }
                finally
                {

                }
            }
        }
    }
}
