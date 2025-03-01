using Microsoft.EntityFrameworkCore.Migrations;

namespace SchoolLink.Server.Migrations
{
    public partial class AddAssignmentCompletion_AddAssignmentDG : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<bool>(
                name: "IsComplete",
                table: "Assignments",
                nullable: false,
                defaultValue: false);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "IsComplete",
                table: "Assignments");
        }
    }
}
