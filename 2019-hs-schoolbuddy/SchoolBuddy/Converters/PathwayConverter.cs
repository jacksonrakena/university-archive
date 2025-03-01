using System;
using SchoolBuddy.Models;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Data;

namespace SchoolBuddy.Converters
{
    public class PathwayConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, string language)
        {
            switch ((CurriculumPathway) value)
            {
                case CurriculumPathway.InternationalBaccalaureate:
                    return "International Baccalaureate (IB)";
                case CurriculumPathway.NationalCertificate:
                    if (parameter is string p && p == "true")
                    {
                        string l = Settings.NceaLevel.FriendlyValue;
                        return "National Certificate of Educational Achievement at " + l;
                    }
                    return "National Certificate of Educational Achievement (NCEA)";
                default:
                    return "Unknown";
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, string language)
        {
            return DependencyProperty.UnsetValue;
        }
    }
}
