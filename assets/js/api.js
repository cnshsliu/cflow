(function() {
  var get_anchor, init_api_page, machinize_text;

  machinize_text = function(text) {
    return text.toLowerCase().replace(/[ ]/g, '-').replace(/\?/g, '').replace(/,/g, '').replace(/'/g, '').slice(0, 45).replace(/-$/, '');
  };

  init_api_page = function() {
    var $api_nav, $api_sections, $bar, all_links, api_width, header_padding, is_scrolling, min_width_page, nav_margin_left, past_header, position_hash, timeout_scrolling;
    position_hash = window.location.hash.slice(1);
    $('.sticky-wrapper:has(.navbar)').waypoint('destroy');
    $('.navbar').removeClass('stuck');
    $('#api-header .sticky-wrapper').height($('#api-command-bar').outerHeight());
    is_scrolling = false;
    timeout_scrolling = null;
    past_header = false;
    header_padding = 10;
    api_width = $('#api-app').width();
    $api_nav = $('#api-nav');
    $api_sections = $('#api-sections');
    $bar = $('#api-command-bar');
    nav_margin_left = 42;
    min_width_page = 961;
    $('#api-header').waypoint({
      offset: header_padding,
      handler: function() {
        var extra_margin;
        past_header = !past_header;
        if (past_header) {
          $bar.addClass('stuck');
          $bar.css({
            'width': api_width,
            'padding-top': header_padding
          });
          $api_nav.css('top', $bar.outerHeight() - 5);
          $api_nav.css('top', $bar.outerHeight() - 5);
          extra_margin = Math.max(($(window).width() - min_width_page) / 2, 0);
          $api_nav.css('left', extra_margin + nav_margin_left - $(window).scrollLeft());
          $bar.css('left', extra_margin + nav_margin_left - $(window).scrollLeft());
          $api_nav.addClass('scrolling');
          return $api_nav.scrollTop(0);
        } else {
          $bar.removeClass('stuck');
          $bar.css({
            'width': 'auto',
            'padding-top': 0
          });
          $api_nav.css('top', 'auto');
          return $api_nav.removeClass('scrolling');
        }
      }
    });
    $(window).on('scroll', function() {
      var extra_margin;
      if (past_header) {
        if ($(document).width() > $(window).width()) {
          extra_margin = Math.max(($(window).width() - min_width_page) / 2, 0);
          $api_nav.css('left', extra_margin + nav_margin_left - $(window).scrollLeft());
          return $bar.css('left', extra_margin + nav_margin_left - $(window).scrollLeft());
        }
      }
    });
    $(window).resize(function() {
      var extra_margin;
      if (past_header) {
        extra_margin = Math.max(($(window).width() - min_width_page) / 2, 0);
        $api_nav.css('left', extra_margin + nav_margin_left - $(window).scrollLeft());
        return $bar.css('left', extra_margin + nav_margin_left - $(window).scrollLeft());
      }
    });
    all_links = $('.nav-list a');
    $.each($('.api-anchor'), function(index, value) {
      return $(value).waypoint({
        offset: 0,
        handler: function() {
          var nav_height, new_active, pixel_offset_from_top, pixels_from_top_of_window, scrolltop_offset, viewport_height;
          if (!is_scrolling) {
            all_links.removeClass('active');
            new_active = $("a[href='#" + ($(value).attr('name')) + "']");
            new_active.addClass('active');
            position_hash = $(value).attr('name');
            viewport_height = $.waypoints('viewportHeight');
            if (new_active[0] != null) {
              pixel_offset_from_top = new_active[0].offsetTop - $api_nav.scrollTop();
              pixels_from_top_of_window = new_active[0].offsetTop + new_active.height() - $api_nav.scrollTop() + $api_nav[0].offsetTop;
              if ((pixel_offset_from_top < 0 || pixels_from_top_of_window > viewport_height) && $('#api-sections').is(':hover')) {
                nav_height = $.waypoints('viewportHeight') - $api_nav[0].offsetTop;
                scrolltop_offset = new_active[0].offsetTop - Math.floor(nav_height / 2);
                return $api_nav.stop().animate({
                  scrollTop: scrolltop_offset
                }, 250);
              }
            }
          }
        }
      });
    });
    $('#api-nav').on('scroll', function() {
      is_scrolling = true;
      if (timeout_scrolling != null) {
        clearTimeout(timeout_scrolling);
      }
      return timeout_scrolling = setTimeout(function() {
        is_scrolling = false;
        return timeout_scrolling = null;
      }, 1000);
    });
    $('.command a').click(function(event) {
      var hash, scrolltop_offset, section;
      event.preventDefault();
      $('.nav-list a').removeClass('active');
      $(event.currentTarget).addClass('active');
      hash = $(event.currentTarget).attr('href').slice(1);
      section = $(event.currentTarget).parent().parent().prev().html();
      scrolltop_offset = $("h1[data-alt='" + section + "']").parent().find(".api-anchor[name='" + hash + "']").offset().top;
      is_scrolling = true;
      return $('html, body').animate({
        scrollTop: scrolltop_offset
      }, 250, 'swing', function() {
        window.location.hash = hash;
        return is_scrolling = false;
      });
    });
    return $('.lang-link').mousedown(function(event) {
      if ((position_hash != null) && position_hash !== '') {
        event.preventDefault();
        event.stopPropagation();
        return window.location = $(event.currentTarget).attr('href') + '#' + position_hash;
      }
    });
  };

  get_anchor = function(question) {
    var anchor;
    return anchor = $(question).attr('href').slice(0, -1);
  };

  $(function() {
    var section_list;
    section_list = $("<ul class='nav nav-list'></ul>");
    $('#api-nav').prepend(section_list);
    $('.apisection').each(function(idx, section) {
      var question_list, section_header, section_item;
      section_header = $('h1', section).data('alt');
      section_item = $("<li class='nav'></li>");
      section_item.append($("<li class='nav-header'>" + section_header + "</li>"));
      question_list = $("<ul class='nav nav-list'></ul>");
      $('h2 a', section).each(function(idx, question) {
        var anchor, question_item;
        question_item = $("<li class='command'></li>");
        anchor = get_anchor(question);
        question_item.append($("<a href='#" + machinize_text(anchor) + "'>" + $(question).text() + "</a>"));
        return question_list.append(question_item);
      });
      section_item.append(question_list);
      return section_list.append(section_item);
    });
    $('h2').each(function(idx, question_header) {
      var wrapper;
      wrapper = $("<div class='api-command'></div>");
      $(question_header).nextUntil("h2").each(function(idx, question_paragraph) {
        return $(question_paragraph).appendTo(wrapper);
      });
      question_header = $(question_header).replaceWith(wrapper);
      return question_header.prependTo(wrapper);
    });
    $('h2 a').each(function(idx, question) {
      var anchor;
      $(question).addClass('slim');
      anchor = get_anchor(question);
      return $("<a class='api-anchor' name='" + machinize_text(anchor) + "'></a>").insertBefore(question);
    });
    return init_api_page();
  });

}).call(this);
