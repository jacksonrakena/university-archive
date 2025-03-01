using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Threading.Tasks;

namespace SchoolLink.Server.Models
{
    public class Assignment
    {
        [Key, DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; }
        public string Name { get; set; }
        public DateTimeOffset DateAssigned { get; set; }
        public DateTimeOffset DateDue { get; set; }
        public User Owner { get; set; }
        public string OwnerId { get; set; }
        public string SubjectId { get; set; }
        public Subject Subject { get; set; }
        public bool IsComplete { get; set; } = false;
    }
}
