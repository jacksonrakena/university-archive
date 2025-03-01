﻿// <auto-generated />
using System;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Migrations;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;
using SchoolLink.Server.Database;

namespace SchoolLink.Server.Migrations
{
    [DbContext(typeof(SchoolLinkDatabaseContext))]
    [Migration("20200222084234_AddRoomDbSet")]
    partial class AddRoomDbSet
    {
        protected override void BuildTargetModel(ModelBuilder modelBuilder)
        {
#pragma warning disable 612, 618
            modelBuilder
                .HasAnnotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn)
                .HasAnnotation("ProductVersion", "3.1.2")
                .HasAnnotation("Relational:MaxIdentifierLength", 63);

            modelBuilder.Entity("SchoolLink.Server.Models.Assignment", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("integer")
                        .HasAnnotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn);

                    b.Property<DateTimeOffset>("DateAssigned")
                        .HasColumnType("timestamp with time zone");

                    b.Property<DateTimeOffset>("DateDue")
                        .HasColumnType("timestamp with time zone");

                    b.Property<bool>("IsComplete")
                        .HasColumnType("boolean");

                    b.Property<string>("Name")
                        .HasColumnType("text");

                    b.Property<string>("OwnerId")
                        .HasColumnType("text");

                    b.Property<string>("SubjectId")
                        .HasColumnType("text");

                    b.HasKey("Id");

                    b.HasIndex("OwnerId");

                    b.HasIndex("SubjectId");

                    b.ToTable("Assignments");
                });

            modelBuilder.Entity("SchoolLink.Server.Models.Room", b =>
                {
                    b.Property<string>("Id")
                        .HasColumnType("text");

                    b.Property<string>("Location")
                        .HasColumnType("text");

                    b.Property<string>("Name")
                        .HasColumnType("text");

                    b.HasKey("Id");

                    b.ToTable("Rooms");
                });

            modelBuilder.Entity("SchoolLink.Server.Models.Subject", b =>
                {
                    b.Property<string>("Id")
                        .HasColumnType("text");

                    b.Property<string>("Name")
                        .HasColumnType("text");

                    b.Property<string>("RoomId")
                        .HasColumnType("text");

                    b.Property<string>("TeacherId")
                        .HasColumnType("text");

                    b.Property<int>("YearLevel")
                        .HasColumnType("integer");

                    b.HasKey("Id");

                    b.HasIndex("RoomId");

                    b.HasIndex("TeacherId");

                    b.ToTable("Subjects");
                });

            modelBuilder.Entity("SchoolLink.Server.Models.Teacher", b =>
                {
                    b.Property<string>("Id")
                        .HasColumnType("text");

                    b.Property<string>("Name")
                        .HasColumnType("text");

                    b.HasKey("Id");

                    b.ToTable("Teachers");
                });

            modelBuilder.Entity("SchoolLink.Server.Models.User", b =>
                {
                    b.Property<string>("Id")
                        .HasColumnType("text");

                    b.Property<string>("FormTeacherId")
                        .HasColumnType("text");

                    b.Property<byte>("House")
                        .HasColumnType("smallint");

                    b.Property<bool>("IsBacca")
                        .HasColumnType("boolean");

                    b.Property<string>("Name")
                        .HasColumnType("text");

                    b.Property<int>("YearLevel")
                        .HasColumnType("integer");

                    b.HasKey("Id");

                    b.HasIndex("FormTeacherId");

                    b.ToTable("Users");
                });

            modelBuilder.Entity("SchoolLink.Server.Models.UserSubject", b =>
                {
                    b.Property<string>("UserId")
                        .HasColumnType("text");

                    b.Property<string>("SubjectId")
                        .HasColumnType("text");

                    b.HasKey("UserId", "SubjectId");

                    b.HasIndex("SubjectId");

                    b.ToTable("UserSubjects");
                });

            modelBuilder.Entity("SchoolLink.Server.Models.Assignment", b =>
                {
                    b.HasOne("SchoolLink.Server.Models.User", "Owner")
                        .WithMany("Assignments")
                        .HasForeignKey("OwnerId");

                    b.HasOne("SchoolLink.Server.Models.Subject", "Subject")
                        .WithMany()
                        .HasForeignKey("SubjectId");
                });

            modelBuilder.Entity("SchoolLink.Server.Models.Subject", b =>
                {
                    b.HasOne("SchoolLink.Server.Models.Room", "Room")
                        .WithMany()
                        .HasForeignKey("RoomId");

                    b.HasOne("SchoolLink.Server.Models.Teacher", "Teacher")
                        .WithMany("Subjects")
                        .HasForeignKey("TeacherId");
                });

            modelBuilder.Entity("SchoolLink.Server.Models.User", b =>
                {
                    b.HasOne("SchoolLink.Server.Models.Teacher", "FormTeacher")
                        .WithMany()
                        .HasForeignKey("FormTeacherId");
                });

            modelBuilder.Entity("SchoolLink.Server.Models.UserSubject", b =>
                {
                    b.HasOne("SchoolLink.Server.Models.Subject", "Subject")
                        .WithMany("Users")
                        .HasForeignKey("SubjectId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("SchoolLink.Server.Models.User", "User")
                        .WithMany("Subjects")
                        .HasForeignKey("UserId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();
                });
#pragma warning restore 612, 618
        }
    }
}
