using System;
using SchoolBuddy.Models;

namespace SchoolBuddy.ViewModels
{
    public class SettingsPageViewModel : ViewModelBase
    {
        public string Name
        {
            get => Settings.Name;
            set
            {
                Settings.Name = value;
                NotifyPropertyChanged(nameof(Name));
            }
        }

        public string AvatarPath
        {
            get => Settings.AvatarPath;
            set
            {
                Settings.AvatarPath = value;
                NotifyPropertyChanged(nameof(AvatarPath));
            }
        }

        public string NationalStudentNumber
        {
            get => Settings.NationalStudentNumber;
            set
            {
                Settings.NationalStudentNumber = value;
                NotifyPropertyChanged(nameof(NationalStudentNumber));
            }
        }

        public string StudentId
        {
            get => Settings.StudentId;
            set
            {
                Settings.StudentId = value;
                NotifyPropertyChanged(nameof(StudentId));
            }
        }

        public TimeSpan DueSoonTime
        {
            get => Settings.DueSoonTime;
            set
            {
                Settings.DueSoonTime = value;
                NotifyPropertyChanged(nameof(DueSoonTime));
            }
        }

        public CurriculumPathway CurriculumPathway
        {
            get => Settings.CurriculumPathway;
            set
            {
                Settings.CurriculumPathway = value;
                NotifyPropertyChanged(nameof(CurriculumPathway));
            }
        }
    }
}
