from django.db import models

from wagtail.core.models import Page
from wagtail.core.fields import RichTextField, StreamField
from wagtail.admin.edit_handlers import FieldPanel, StreamFieldPanel, MultiFieldPanel
from wagtail.images.blocks import ImageChooserBlock
from wagtail.core import blocks
from modelcluster.fields import ParentalKey
from modelcluster.contrib.taggit import ClusterTaggableManager
from taggit.models import TaggedItemBase


class HomePage(Page):
    body = RichTextField(blank=True)

    content_panels = Page.content_panels + [
        FieldPanel('body', classname='full')
    ]

class NewsTag(TaggedItemBase):
    content_object = ParentalKey(
        'NewsPage',
        related_name='tagged_items',
        on_delete=models.CASCADE
    )

class NewsPage(Page):
    author = models.CharField(verbose_name="Author full name", max_length=255)
    authorKey = models.CharField(verbose_name="Author username (i.e. rakenaj)", max_length=100)
    date = models.DateField("Date")
    tagline = models.CharField(verbose_name="Tagline", max_length=300)
    show_on_front_page = models.BooleanField(verbose_name="Show on front page", default=True)
    body = StreamField([
        ('heading', blocks.CharBlock(form_classname="full title")),
        ('paragraph', blocks.RichTextBlock()),
        ('image', ImageChooserBlock()),
    ])
    tags = ClusterTaggableManager(through=NewsTag, blank=True)

    content_panels = Page.content_panels + [
        FieldPanel('tagline'),
        MultiFieldPanel([
            FieldPanel('date'),
            FieldPanel('tags'),
            FieldPanel('show_on_front_page')
        ], heading="Data Control"),
        FieldPanel('author'),
        FieldPanel('authorKey'),
        StreamFieldPanel('body')
    ]