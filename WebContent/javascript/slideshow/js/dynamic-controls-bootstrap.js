/**
*	Bootstrap the javascript
*/

$(document).ready(function () {
	
	var opts = {
		controls: {
			previousSlide: {auto: true},
			playPause: {auto: true},
			nextSlide: {auto: true},
		}
	};
	$('.rs-slideshow').rsfSlideshow(opts);
	
});
