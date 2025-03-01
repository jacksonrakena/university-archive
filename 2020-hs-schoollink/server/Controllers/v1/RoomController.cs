using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using SchoolLink.Server.Database;
using SchoolLink.Server.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SchoolLink.Server.Controllers.v1
{
    [ApiController]
    [Route("api/v1/rooms")]
    public class RoomController : SchoolLinkController
    {
        public SchoolLinkDatabaseContext _context;

        public RoomController(SchoolLinkDatabaseContext context)
        {
            _context = context;
        }

        [HttpGet, Authorize]
        public List<Room> GetRooms()
        {
            return _context.Rooms.ToList();
        }
    }
}
