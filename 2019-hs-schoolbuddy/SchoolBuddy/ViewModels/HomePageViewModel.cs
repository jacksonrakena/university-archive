using SchoolBuddy.Models;

namespace SchoolBuddy.ViewModels
{
    public class HomePageViewModel : ViewModelBase
    {
        public string Name => Settings.Name;

        public string AvatarUrl => Settings.AvatarPath;

        public CurriculumPathway CurriculumPathway => Settings.CurriculumPathway;
    }
}
