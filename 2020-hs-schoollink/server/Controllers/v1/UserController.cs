using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SchoolLink.Server.Database;
using SchoolLink.Server.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;

namespace SchoolLink.Server.Controllers.v1
{
    [ApiController]
    [Route("api/v1/users")]
    public class UserController : SchoolLinkController
    {
        private readonly SchoolLinkDatabaseContext _context;

        public UserController(SchoolLinkDatabaseContext context)
        {
            _context = context;
        }

        [HttpGet("me"), Authorize]
        public async Task<object> GetMeAsync()
        {
            var user = await _context.Users
                .Include(c => c.Assignments)
                .Include(c => c.FormTeacher)
                .Include(c => c.Subjects)
                .ThenInclude(c => c.Subject)
                .FirstAsync(c => c.Id == UserId);
            if (user == null) return new
            {
                error = "user_not_found",
                description = "That user does not exist in the SchoolLink database."
            };
            return new
            {
                school = "SCNZ1",
                user.Id,
                user.Name,
                user.IsBacca,
                user.House,
                user.YearLevel,
                subjects = user.Subjects.Select(c => new
                {
                    id = c.SubjectId,
                    teacherId = c.Subject.TeacherId,
                    name = c.Subject.Name,
                    room = c.Subject.RoomId
                }),
                assignments = user.Assignments.Select(c => new
                {
                    id = c.Id,
                    dateDue = c.DateDue,
                    dateAssigned = c.DateAssigned,
                    c.Name,
                    c.IsComplete,
                    subject = c.SubjectId
                })
            };
        }
    }
}
