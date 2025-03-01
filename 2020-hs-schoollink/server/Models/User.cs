using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SchoolLink.Server.Models
{
    public class User
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public List<UserSubject> Subjects { get; set; }
        public List<Assignment> Assignments { get; set; }
        public House House { get; set; }
        public Teacher FormTeacher { get; set; }
        public string FormTeacherId { get; set; }
        public int YearLevel { get; set; }
        public bool IsBacca { get; set; }
    }
}
 