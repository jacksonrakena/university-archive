using System;
using System.Collections.ObjectModel;
using System.ComponentModel;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Windows.Storage;

namespace SchoolBuddy.Models
{
    public class Assignment : IEditableObject
    {
        public static ObservableCollection<Assignment> Assignments = new ObservableCollection<Assignment>();
        public static ApplicationDataContainer AssignmentsContainer = Data.GetContainer("assignments");

        public static void LoadAssignments()
        {
            foreach (System.Collections.Generic.KeyValuePair<string, object> a in AssignmentsContainer.Values)
            {
                JObject token = JObject.Parse(a.Value.ToString());
                Assignment data = new Assignment
                {
                    Name = token["Name"].ToObject<string>(),
                    DateIssued = token["DateIssued"].ToObject<DateTimeOffset>(),
                    DueDate = token["DueDate"].ToObject<DateTimeOffset>(),
                    Guid = Guid.Parse(a.Key)
                };
                UpdateAssignment(data);
            }
        }

        public static void UpdateAssignment(Assignment data)
        {
            if (!Assignments.Contains(data))
            {
                Assignments.Add(data);
            }

            AssignmentsContainer.Values[data.Guid.ToString()] = JsonConvert.SerializeObject(data);
        }

        public static void DeleteAssignment(Assignment data)
        {
            Assignments.Remove(data);
            AssignmentsContainer.Values.Remove(data.Guid.ToString());
        }

        public DateTimeOffset DueDate { get; set; }
        public DateTimeOffset DateIssued { get; set; }
        public string Name { get; set; }
        public Guid Guid { get; set; }

        public void BeginEdit()
        {
        }

        public void CancelEdit()
        {
        }

        public void EndEdit()
        {
            UpdateAssignment(this);
        }
    }
}
