using System;
using Windows.Foundation.Collections;
using Windows.Storage;

namespace SchoolBuddy.Models
{
    public static class Settings
    {
        private static ApplicationDataContainer _settingsContainer;

        public static ApplicationDataContainer GetSettingsContainer()
        {
            if (_settingsContainer == null)
            {
                _settingsContainer = Data.GetContainer("settings");
            }

            return _settingsContainer;
        }

        public static IPropertySet GetSettings()
        {
            return GetSettingsContainer().Values;
        }

        public static object GetSetting(string name, object valueToSetIfNotExist = null)
        {
            IPropertySet settings = GetSettings();
            if (!settings.ContainsKey(name))
            {
                if (valueToSetIfNotExist == null)
                {
                    return null;
                }

                settings[name] = valueToSetIfNotExist;
            }
            return settings[name];
        }

        public static T GetSetting<T>(string name, T valueToSetIfNotExist = null) where T : class
        {
            return (T) GetSetting(name, (object) valueToSetIfNotExist);
        }

        public static T GetSetting<T>(string name, T? valueToSetIfNotExist = null) where T : struct
        {
            return valueToSetIfNotExist.HasValue 
                ? (T) GetSetting(name, valueToSetIfNotExist.Value)
                : (T) GetSetting(name);
        }

        public static void SetSetting(string name, object value)
        {
            GetSettings()[name] = value;
        }

        public static string Name
        {
            get => GetSetting("Name", "No name set");
            set => SetSetting("Name", value);
        }

        public static string StudentId
        {
            get => GetSetting("StudentId", (string) null);
            set => SetSetting("StudentId", value);
        }

        public static string NationalStudentNumber
        {
            get => GetSetting("NSN", (string) null);
            set => SetSetting("NSN", value);
        }
        public static string AvatarPath
        {
            get => GetSetting("AvatarPath", (string) null);
            set => SetSetting("AvatarPath", value);
        }

        public static TimeSpan DueSoonTime
        {
            get => GetSetting<TimeSpan>("DueSoonTime", TimeSpan.FromDays(7));
            set => SetSetting("DueSoonTime", value);
        }

        public static CurriculumPathway CurriculumPathway
        {
            get => Enum.Parse<CurriculumPathway>(GetSetting("Pathway", CurriculumPathway.NationalCertificate.ToString()));
            set => SetSetting("Pathway", value.ToString());
        }

        public static SettingsNotifyPropertyChanged<string, NceaLevel> NceaLevel = new SettingsEnumNotifyPropertyChanged<NceaLevel>(
            "NceaLevel", Models.NceaLevel.One, c =>
            {
                switch (c)
                {
                    case Models.NceaLevel.One:
                        return "Level 1";
                    case Models.NceaLevel.Two:
                        return "Level 2";
                    case Models.NceaLevel.Three:
                        return "Level 3";
                    default:
                        return "Unknown";
                }
            });
    }
}
