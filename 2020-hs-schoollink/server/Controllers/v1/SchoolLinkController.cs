using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;

namespace SchoolLink.Server.Controllers.v1
{
    public class SchoolLinkController : ControllerBase
    {
        public string UserId => User.FindFirst(ClaimTypes.NameIdentifier).Value;
    }
}
