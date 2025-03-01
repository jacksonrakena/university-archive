using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using SchoolLink.Server.Controllers.v1;
using SchoolLink.Server.Database;
using SchoolLink.Server.Models;

namespace SchoolLink.Server.Controllers
{
    [ApiController]
    [Route("api/v1/hello")]
    public class HelloController : SchoolLinkController
    {
        private readonly SchoolLinkDatabaseContext _context;
        
        public HelloController(SchoolLinkDatabaseContext context)
        {
            _context = context;
        }

        [HttpGet]
        public object Get()
        {
            return new
            {
                requestAcknowledged = true,
                serverTime = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds(),
                softwareVersion = Program.version.ToString()
            };
        }

        [HttpGet("authenticated"), Authorize]
        public async Task<object> HelloAuthenticated()
        {
            var user = await _context.Users.FindAsync(UserId);
            return new
            {
                authenticated = true,
                userId = User.FindFirst(ClaimTypes.NameIdentifier).Value,
                userExists = user != null
            };
        }
    }
}
