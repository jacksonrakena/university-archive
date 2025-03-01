using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SchoolLink.Server.Models
{
    public class Subject
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public Teacher Teacher { get; set; }
        public string TeacherId { get; set; }
        public int YearLevel { get; set; }
        public List<UserSubject> Users { get; set; }
        public string RoomId { get; set; }
        public Room Room { get; set; }
    }
}
