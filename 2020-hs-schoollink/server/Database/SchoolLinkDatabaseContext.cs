using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using SchoolLink.Server.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SchoolLink.Server.Database
{
    public class SchoolLinkDatabaseContext : DbContext
    {
        public DbSet<User> Users { get; set; }
        public DbSet<Subject> Subjects { get; set; }
        public DbSet<Assignment> Assignments { get; set; }
        public DbSet<Teacher> Teachers { get; set; }
        public DbSet<UserSubject> UserSubjects { get; set; }
        public DbSet<Room> Rooms { get; set; }

        public SchoolLinkDatabaseContext(DbContextOptions<SchoolLinkDatabaseContext> options) : base(options)
        {
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Teacher>().HasMany(c => c.Subjects).WithOne(c => c.Teacher);

            modelBuilder.Entity<User>().HasMany(c => c.Assignments).WithOne(c => c.Owner);
            modelBuilder.Entity<User>().Property(e => e.House).HasConversion(new EnumToNumberConverter<House, byte>());

            modelBuilder.Entity<UserSubject>()
                .HasKey(t => new { t.UserId, t.SubjectId });

            modelBuilder.Entity<UserSubject>()
                .HasOne(a => a.User)
                .WithMany(a => a.Subjects)
                .HasForeignKey(a => a.UserId);

            modelBuilder.Entity<UserSubject>()
                .HasOne(a => a.Subject)
                .WithMany(a => a.Users)
                .HasForeignKey(a => a.SubjectId);
        }
    }
}
