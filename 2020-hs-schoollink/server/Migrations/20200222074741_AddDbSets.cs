using Microsoft.EntityFrameworkCore.Migrations;

namespace SchoolLink.Server.Migrations
{
    public partial class AddDbSets : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Assignment_User_OwnerId",
                table: "Assignment");

            migrationBuilder.DropForeignKey(
                name: "FK_Assignment_Subject_SubjectId",
                table: "Assignment");

            migrationBuilder.DropForeignKey(
                name: "FK_Subject_Teacher_TeacherId",
                table: "Subject");

            migrationBuilder.DropForeignKey(
                name: "FK_User_Teacher_FormTeacherId",
                table: "User");

            migrationBuilder.DropForeignKey(
                name: "FK_UserSubject_Subject_SubjectId",
                table: "UserSubject");

            migrationBuilder.DropForeignKey(
                name: "FK_UserSubject_User_UserId",
                table: "UserSubject");

            migrationBuilder.DropPrimaryKey(
                name: "PK_UserSubject",
                table: "UserSubject");

            migrationBuilder.DropPrimaryKey(
                name: "PK_User",
                table: "User");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Teacher",
                table: "Teacher");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Subject",
                table: "Subject");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Assignment",
                table: "Assignment");

            migrationBuilder.RenameTable(
                name: "UserSubject",
                newName: "UserSubjects");

            migrationBuilder.RenameTable(
                name: "User",
                newName: "Users");

            migrationBuilder.RenameTable(
                name: "Teacher",
                newName: "Teachers");

            migrationBuilder.RenameTable(
                name: "Subject",
                newName: "Subjects");

            migrationBuilder.RenameTable(
                name: "Assignment",
                newName: "Assignments");

            migrationBuilder.RenameIndex(
                name: "IX_UserSubject_SubjectId",
                table: "UserSubjects",
                newName: "IX_UserSubjects_SubjectId");

            migrationBuilder.RenameIndex(
                name: "IX_User_FormTeacherId",
                table: "Users",
                newName: "IX_Users_FormTeacherId");

            migrationBuilder.RenameIndex(
                name: "IX_Subject_TeacherId",
                table: "Subjects",
                newName: "IX_Subjects_TeacherId");

            migrationBuilder.RenameIndex(
                name: "IX_Assignment_SubjectId",
                table: "Assignments",
                newName: "IX_Assignments_SubjectId");

            migrationBuilder.RenameIndex(
                name: "IX_Assignment_OwnerId",
                table: "Assignments",
                newName: "IX_Assignments_OwnerId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_UserSubjects",
                table: "UserSubjects",
                columns: new[] { "UserId", "SubjectId" });

            migrationBuilder.AddPrimaryKey(
                name: "PK_Users",
                table: "Users",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Teachers",
                table: "Teachers",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Subjects",
                table: "Subjects",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Assignments",
                table: "Assignments",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_Assignments_Users_OwnerId",
                table: "Assignments",
                column: "OwnerId",
                principalTable: "Users",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_Assignments_Subjects_SubjectId",
                table: "Assignments",
                column: "SubjectId",
                principalTable: "Subjects",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_Subjects_Teachers_TeacherId",
                table: "Subjects",
                column: "TeacherId",
                principalTable: "Teachers",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_Users_Teachers_FormTeacherId",
                table: "Users",
                column: "FormTeacherId",
                principalTable: "Teachers",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_UserSubjects_Subjects_SubjectId",
                table: "UserSubjects",
                column: "SubjectId",
                principalTable: "Subjects",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_UserSubjects_Users_UserId",
                table: "UserSubjects",
                column: "UserId",
                principalTable: "Users",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Assignments_Users_OwnerId",
                table: "Assignments");

            migrationBuilder.DropForeignKey(
                name: "FK_Assignments_Subjects_SubjectId",
                table: "Assignments");

            migrationBuilder.DropForeignKey(
                name: "FK_Subjects_Teachers_TeacherId",
                table: "Subjects");

            migrationBuilder.DropForeignKey(
                name: "FK_Users_Teachers_FormTeacherId",
                table: "Users");

            migrationBuilder.DropForeignKey(
                name: "FK_UserSubjects_Subjects_SubjectId",
                table: "UserSubjects");

            migrationBuilder.DropForeignKey(
                name: "FK_UserSubjects_Users_UserId",
                table: "UserSubjects");

            migrationBuilder.DropPrimaryKey(
                name: "PK_UserSubjects",
                table: "UserSubjects");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Users",
                table: "Users");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Teachers",
                table: "Teachers");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Subjects",
                table: "Subjects");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Assignments",
                table: "Assignments");

            migrationBuilder.RenameTable(
                name: "UserSubjects",
                newName: "UserSubject");

            migrationBuilder.RenameTable(
                name: "Users",
                newName: "User");

            migrationBuilder.RenameTable(
                name: "Teachers",
                newName: "Teacher");

            migrationBuilder.RenameTable(
                name: "Subjects",
                newName: "Subject");

            migrationBuilder.RenameTable(
                name: "Assignments",
                newName: "Assignment");

            migrationBuilder.RenameIndex(
                name: "IX_UserSubjects_SubjectId",
                table: "UserSubject",
                newName: "IX_UserSubject_SubjectId");

            migrationBuilder.RenameIndex(
                name: "IX_Users_FormTeacherId",
                table: "User",
                newName: "IX_User_FormTeacherId");

            migrationBuilder.RenameIndex(
                name: "IX_Subjects_TeacherId",
                table: "Subject",
                newName: "IX_Subject_TeacherId");

            migrationBuilder.RenameIndex(
                name: "IX_Assignments_SubjectId",
                table: "Assignment",
                newName: "IX_Assignment_SubjectId");

            migrationBuilder.RenameIndex(
                name: "IX_Assignments_OwnerId",
                table: "Assignment",
                newName: "IX_Assignment_OwnerId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_UserSubject",
                table: "UserSubject",
                columns: new[] { "UserId", "SubjectId" });

            migrationBuilder.AddPrimaryKey(
                name: "PK_User",
                table: "User",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Teacher",
                table: "Teacher",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Subject",
                table: "Subject",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Assignment",
                table: "Assignment",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_Assignment_User_OwnerId",
                table: "Assignment",
                column: "OwnerId",
                principalTable: "User",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_Assignment_Subject_SubjectId",
                table: "Assignment",
                column: "SubjectId",
                principalTable: "Subject",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_Subject_Teacher_TeacherId",
                table: "Subject",
                column: "TeacherId",
                principalTable: "Teacher",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_User_Teacher_FormTeacherId",
                table: "User",
                column: "FormTeacherId",
                principalTable: "Teacher",
                principalColumn: "Id",
                onDelete: ReferentialAction.Restrict);

            migrationBuilder.AddForeignKey(
                name: "FK_UserSubject_Subject_SubjectId",
                table: "UserSubject",
                column: "SubjectId",
                principalTable: "Subject",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_UserSubject_User_UserId",
                table: "UserSubject",
                column: "UserId",
                principalTable: "User",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
