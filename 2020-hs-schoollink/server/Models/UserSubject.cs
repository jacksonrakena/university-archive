using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SchoolLink.Server.Models
{
    public class UserSubject
    {
        public string SubjectId { get; set; }
        public Subject Subject { get; set; }

        public string UserId { get; set; }
        public User User { get; set; }
    }
}
