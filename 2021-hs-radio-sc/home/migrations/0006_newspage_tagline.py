# Generated by Django 3.2.3 on 2021-05-21 02:05

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('home', '0005_auto_20210521_1348'),
    ]

    operations = [
        migrations.AddField(
            model_name='newspage',
            name='tagline',
            field=models.CharField(default='The results from the previous week, and the draw for the week starting on the 24th of May.', max_length=300, verbose_name='Tagline'),
            preserve_default=False,
        ),
    ]
