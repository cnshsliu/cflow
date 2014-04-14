(function() {
  var rewrite_links;

  rewrite_links = function() {
    var href, lang, link, links_on_page, routes, _i, _len, _results;
    lang = $.cookie('lang');
    if (lang === null) {
      lang = 'javascript';
      $.cookie('lang', lang, {
        path: '/'
      });
    } else if (/javascript/.test(document.location.pathname)) {
      if (lang !== 'javascript') {
        lang = 'javascript';
        $.cookie('lang', lang, {
          path: '/'
        });
      }
    } else if (/python/.test(document.location.pathname)) {
      if (lang !== 'python') {
        lang = 'python';
        $.cookie('lang', lang, {
          path: '/'
        });
      }
    } else if (/ruby/.test(document.location.pathname)) {
      if (lang !== 'ruby') {
        lang = 'ruby';
        $.cookie('lang', lang, {
          path: '/'
        });
      }
    }
    routes = {
      '/docs/guide/': true,
      '/docs/cookbook/': true,
      '/api/': true
    };
    links_on_page = $('a');
    _results = [];
    for (_i = 0, _len = links_on_page.length; _i < _len; _i++) {
      link = links_on_page[_i];
      href = $(link).attr('href');
      if (routes[href] != null) {
        _results.push($(link).attr('href', href + lang + '/'));
      } else {
        _results.push(void 0);
      }
    }
    return _results;
  };

  $(function() {
    return rewrite_links();
  });

}).call(this);
