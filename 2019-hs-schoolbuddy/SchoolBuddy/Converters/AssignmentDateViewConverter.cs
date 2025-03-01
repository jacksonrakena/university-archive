using System;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Data;

namespace SchoolBuddy.Converters
{
    public class AssignmentDateViewConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, string language)
        {
            if (value == null)
            {
                return null;
            }

            DateTime dt = DateTime.Parse(value.ToString());
            return dt.ToString("D");
        }

        public object ConvertBack(object value, Type targetType, object parameter, string language)
        {
            return DependencyProperty.UnsetValue;
        }
    }
}
