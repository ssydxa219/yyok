jQuery(document).ready(function () {


    $('button[data-loading-text]')
            .on('click', function () {
                var btn = $(this);
                btn.button('loading');
                setTimeout(function () {
                    btn.button('reset');
                }, 3000);
            });


    $('#contactform').submit(function () {

        var action = $(this).attr('action');

        $("#error-message").slideUp(750, function () {
            $('#error-message').hide();
            $.post(action, {
                name: $('#name').val(),
                email: $('#email').val(),
                subject: $('#subject').val(),
                message: $('#message').val()
            },
            function (data) {
                document.getElementById('error-message').innerHTML = data;
                $('#error-message').slideDown('slow');
                $('#submit').removeAttr('disabled');
                if (data.match('success') != null)
                    $('#contactform').slideUp('slow');

            }
            );

        });

        return false;

    });
    $('#home-contactform').submit(function () {

        var action = $(this).attr('action');

        $("#home-message").slideUp(750, function () {
            $('#home-message').hide();
            $.post(action, {
                names: $('#names').val(),
                emails: $('#emails').val(),
                tel: $('#tel').val(),
                messages: $('#messages').val()
            },
            function (data) {
                document.getElementById('home-message').innerHTML = data;
                $('#home-message').slideDown('slow');
                $('#submit').removeAttr('disabled');
                if (data.match('success') != null)
                    $('#home-contactform').slideUp('slow');

            }
            );

        });

        return false;

    });

    $('#callback').submit(function () {

        var action = $(this).attr('action');

        $("#home-error").slideUp(750, function () {
            $('#home-error').hide();
            $.post(action, {
                names: $('#names').val(),
                emails: $('#emails').val(),
                numbers: $('#numbers').val()
            },
            function (data) {
                document.getElementById('home-error').innerHTML = data;
                $('#home-error').slideDown('slow');
                $('#submit').removeAttr('disabled');
                if (data.match('success') != null)
                    $('#callback').slideUp('slow');

            }
            );

        });

        return false;

    });


    //GET THE FORM
    var form = $('#ajax-contact');

    //GET FORM MESSAGES
    var formMessages = $('#form-messages');

    $(form).submit(function(event) {
        //STOP BROWSER FORM SUBMISSION
        event.preventDefault();

        //SERIALIZE THE FORM DATA
        var formData = $(form).serialize();

        $.ajax({
            type: 'POST',
            url: $(form).attr('action'),
            data: formData
        })

        .done(function(response) {
            // Make sure that the formMessages div has the 'success' class.
            $(formMessages).removeClass('alert alert-danger');
            $(formMessages).addClass('alert alert-success');

            // Set the message text.
            $(formMessages).text(response);

            // Clear the form.
            $('#name').val('');
            $('#email').val('');
            $('#phone').val('');
            $('#message').val('');
        })

        .fail(function(data) {
            // Make sure that the formMessages div has the 'error' class.
            $(formMessages).removeClass('alert alert-success');
            $(formMessages).addClass('alert alert-danger');

            // Set the message text.
            if (data.responseText !== '') {
                $(formMessages).text(data.responseText);
            } else {
                $(formMessages).text('Oops! An error occured and your message could not be sent.');
            }
        });

    });





});