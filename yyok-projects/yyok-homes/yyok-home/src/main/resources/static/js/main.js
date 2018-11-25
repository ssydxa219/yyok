
(function($) {
	
	"use strict";
	


		/* ==============================================
	    Preloader -->
	     =============================================== */

	    $(window).load(function () {
	        $("#status").fadeOut();
	        $("#preloader").delay(350).fadeOut("slow");
	        $("body").delay(350).css({ "overflow": "visible" });

	    });

	   
	   /* ==============================================
	    Menu hide/show on scroll -->
	     =============================================== */
	
		$('#slides').superslides({
		  animation: 'fade',
		  play:7000, // change value if you want to increase or decrese speed
		  animation_speed:800 // change time interval during slide change
		});




	/* ==============================================
	    Menu hide/show on scroll -->
	     =============================================== */
	    var ost = 0;
	    $(window).scroll(function() {

	        var cOst = $(this).scrollTop();

	        if(cOst > 300){
	              $('nav').addClass('nav-background');

	        }else{
	              $('nav').removeClass('nav-background');
	        }

	        if(cOst == 0)
	        {
	            $('.navbar').addClass('top-nav-collapse');

	        } else if(cOst > ost)
	        {
	            $('.navbar').addClass('top-nav-collapse').removeClass('default');
	        } else 
	        {
	            $('.navbar').addClass('default').removeClass('top-nav-collapse');
	        }
	        ost = cOst;
	    });

	    /* End of Menu hide/show on scroll */ 
	    

	   /* ==============================================
	     Target menu section  -->
	     =============================================== */

	  $('.navbar, .home-btn, .holder ').find('a[href*=#]:not([href=#])').on('click', function () {
	        if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '') && location.hostname == this.hostname) {
	            var target = $(this.hash);
	            target = target.length ? target : $('[name=' + this.hash.slice(1) + ']');
	            if (target.length) {
	                $('html,body').animate({
	                    scrollTop: (target.offset().top - 0)
	                }, 1000);
	                if ($('.navbar-toggle').css('display') != 'none') {
	                    $(this).parents('.container').find(".navbar-toggle").trigger("click");
	                }
	                return false;
	            }
	        }     
	    });


    /* ==============================================
     Mailchimp  -->
     =============================================== */

    $('#mc-form').ajaxChimp({
        url: 'http://codextree.us10.list-manage.com/subscribe/post?u=b02e4f21e264536eff4820875&amp;id=4d57faf2cb'
            /* Replace Your AjaxChimp Subscription Link */
    });


     /* ==============================================
     scorll animation  -->
     =============================================== */

      window.sr = ScrollReveal();

      sr.reveal('.reveal-bottom', {
        origin: 'bottom',
        distance: '20px',
        duration: 800,
        delay: 400,
        opacity: 1,
        scale: 0,
        easing: 'linear',
        reset: true
      });

      sr.reveal('.reveal-top', {
        origin: 'top',
        distance: '20px',
        duration: 800,
        delay: 400,
        opacity: 1,
        scale: 0,
        easing: 'linear',
        reset: true
      });

      sr.reveal('.reveal-left', {
        origin: 'left',
        distance: '20px',
        duration: 800,
        delay: 400,
        opacity: 1,
        scale: 0,
        easing: 'linear',
        reset: true
      });

      sr.reveal('.reveal-right', {
        origin: 'right',
        distance: '20px',
        duration: 800,
        delay: 400,
        opacity: 1,
        scale: 0,
        easing: 'linear',
        reset: true
      });
        
      sr.reveal('.reveal-left-fade', {
        origin: 'left',
        distance: '20px',
        duration: 800,
        delay: 0,
        opacity: 0,
        scale: 0,
        easing: 'linear',
        mobile: false
      });

      sr.reveal('.reveal-right-fade', {
        origin: 'right',
        distance: '20px',
        duration: 800,
        delay: 0,
        opacity: 0,
        scale: 0,
        easing: 'linear',
        mobile: false
      });
        
      sr.reveal('.reveal-right-delay', {
        origin: 'right',
        distance: '20px',
        duration: 1000,
        delay: 0,
        opacity: 0,
        scale: 0,
        easing: 'linear',
        mobile: false
      }, 500);
        
      sr.reveal('.reveal-bottom-fade', {
        origin: 'bottom',
        distance: '20px',
        duration: 800,
        delay: 0,
        opacity: 0,
        scale: 0,
        easing: 'linear',
        mobile: false
      });


	
	
	
	
})(window.jQuery);



	  // google map
      function initMap() {
        var chicago = {lat: 41.85, lng: -87.65};
        var indianapolis = {lat: 39.79, lng: -86.14};

        var map = new google.maps.Map(document.getElementById('gmap'), {
          center: chicago,
          scrollwheel: false,
          zoom: 7
        });

        var directionsDisplay = new google.maps.DirectionsRenderer({
          map: map
        });

        // Set destination, origin and travel mode.
        var request = {
          destination: indianapolis,
          origin: chicago,
          travelMode: 'DRIVING'
        };

        // Pass the directions request to the directions service.
        var directionsService = new google.maps.DirectionsService();
        directionsService.route(request, function(response, status) {
          if (status == 'OK') {
            // Display the route on the map.
            directionsDisplay.setDirections(response);
          }
        });
      }
