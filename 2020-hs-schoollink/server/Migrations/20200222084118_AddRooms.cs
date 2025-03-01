using Microsoft.EntityFrameworkCore.Migrations;

namespace SchoolLink.Server.Migrations
{
    public partial class AddRooms : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "RoomId",
                table: "Subjects",
                nullable: true);

            migrationBuilder.CreateTable(
                name: "Room",
                columns: table => new
                {
                    Id = table.Column<string>(nullable: false),
                    Name = table.Column<string>(nullable: true),
                    Location = table.Column<string>(nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Room", x => x.Id);
                });

            migrationBuilder.CreateIndex(
                name: "IX_Subjects_RoomId",
                table: "Subjects",
                column: "RoomId");

            migrationBuilder.AddForeignKey(
                name: "FK_Subjects_Room_RoomId",
                table: "Subjects",
                column: "RoomId",
                principalTable: "Room",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Subjects_Room_RoomId",
                table: "Subjects");

            migrationBuilder.DropTable(
                name: "Room");

            migrationBuilder.DropIndex(
                name: "IX_Subjects_RoomId",
                table: "Subjects");

            migrationBuilder.DropColumn(
                name: "RoomId",
                table: "Subjects");
        }
    }
}
