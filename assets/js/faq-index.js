/*
 *
 */
function machinize_text(text) {
    return text.toLowerCase().replace(/ /g, '-').replace(/\?/g, '').replace(/,/g, '').replace(/'/g, '').slice(0, 45).replace(/-$/, '');
}

$(function() {
    // First, create the table of contents
    var section_list = $("<ul class='section-list'></ul>");
    $('#faqcontents').append(section_list);
    // Go through each section to add it to the table of contents
    $('.faqsection').each(function(idx, section) {
        // Add the section header to table of contents
        var section_header = $('h1', section).data('alt');
        var section_item = $("<li class='section-item'></li>");
        section_item.append($("<h1 class='section-header'>" + section_header + "</h1>"));

        // Add the list of faq questions
        var question_list = $("<ul class='question-list'></ul>");
        $('h2', section).each(function(idx, question) {
            var question_item = $("<li class='question-item'></li>");
            question_item.append($("<a href='#" + machinize_text($(question).text()) + "'>" + $(question).text() + "</a>"));
            question_list.append(question_item);
        });
        section_item.append(question_list);

        // Add the section to table of contents
        section_list.append(section_item);
    });

    // Wrap each question in a div (for styling)
    $('h2').each(function(idx, question_header) {
        var wrapper = $("<div class='faq-question'></div>");
        $(question_header).nextUntil("h2").each(function(idx, question_paragraph) {
            $(question_paragraph).appendTo(wrapper);
        });
        question_header = $(question_header).replaceWith(wrapper);
        question_header.prependTo(wrapper);
    });

    // Finally, inject anchors into faq questions and add a style class ("slim")
    $('h2').each(function(idx, question) {
        $(question).addClass('slim');
        var qtext = $(question).text();
        $("<a class='faq-anchor' name='" + machinize_text(qtext) + "'></a>").insertBefore(question);
    });
});
