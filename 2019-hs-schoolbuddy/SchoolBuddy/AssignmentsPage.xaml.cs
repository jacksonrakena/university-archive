using System;
using System.Collections.ObjectModel;
using SchoolBuddy.Models;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace SchoolBuddy
{
    public sealed partial class AssignmentsPage : Page
    {
        private ObservableCollection<Assignment> Assignments => Assignment.Assignments;

        public AssignmentsPage()
        {
            InitializeComponent();
        }

        private async void Button_Click(object sender, RoutedEventArgs e)
        {
            await AssignmentCreateDialog.ShowAsync();
        }

        private void AssignmentCreateDialog_PrimaryButtonClick(ContentDialog sender, ContentDialogButtonClickEventArgs args)
        {
            Assignment assignment = new Assignment
            {
                Name = AssignmentName.Text,
                DateIssued = DateIssued.Date,
                DueDate = DateDue.Date,
                Guid = Guid.NewGuid()
            };
            Assignment.UpdateAssignment(assignment);
        }

        private void AssignmentCreateDialog_Opened(ContentDialog sender, ContentDialogOpenedEventArgs args)
        {
            DateIssued.Date = DateTime.Now;
            DateDue.SelectedDate = null;
            AssignmentName.Text = string.Empty;
        }
    }
}
