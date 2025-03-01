using System;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Data;

namespace SchoolBuddy.Converters
{
    public class DueSoonTimeConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, string language)
        {
            TimeSpan b = (TimeSpan) value;
            switch (b.TotalDays)
            {
                case 1:
                    return "1 day";
                case 5:
                    return "5 days";
                case 7:
                    return "1 week";
                case 14:
                    return "2 weeks";
                case 30:
                    return "1 month";
                case 180:
                    return "6 months";
                default:
                    return $"{b.TotalDays} days";
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, string language)
        {
            return DependencyProperty.UnsetValue;
        }
    }
}
