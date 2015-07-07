(function() {
	if (window.CloudOSBootstrap.noLoadingScreen) {
		return
	}
	var go12 = /msie 8/.test(navigator.userAgent.toLowerCase()), pos = 0, size = 32, uri;
	if (go12) {
		uri = "/system/cloudos/15CPlus63/cloudos_foundation/15CPlus63/en-us/source/resources/images/spinner_white_32.png"
	} else {
		uri = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAADACAMAAACDB5U0AAAAMFBMVEUAAAD///////////////////////////////////////////////////////////87TQQwAAAAEHRSTlMAUQwWSGr/BSODz+iYN7ioo0T1UwAABSpJREFUeNrtmouSozgMRbFlYfn9/387FoaIJFAYU7s7W5Vb3ZOkMzrIliD2JdNPf79UCOpJPMxV8ABADKC7UakUOgLolHRPvPbz7OEbYI1SxvaMO4Qwu28AqUroAdg4VwR9ArSqgNQ3c5xC+QQkVaWnLrm5Esw7ADi+obqmMczZvgGMMjyFnVKcAu4B1JOAlZbLNQUPAtDmbQaPM0nZO9qecwqutXKAtYTwaihz1FG2BB88ru8spYTtZLK7ElqqLJWOAd6HrNZS1nZkGEBrwq2EwP3ItKMhBM+IuLxpciF5D4yB5ZG7gWlwWD3nlyS8g5NZ3sJPT6oUQ0McZNiyZyWYzmXaTBR7cHzDg1eGLpoBawIhHgOq6LobwflCx0OQwV8g7El2HeHjApGeTmX1Sx9ZmiwqUsSTQrLMezHzTl5qcFAHIQwCzOvno4G6hqBOhzDpgUn85wRndNsXjtEdn+7J0DVCq1hixtMa0EV8cqXEWE4A5upqQC28HA8Brq5HgLGG19+jj0C5Ip6Og1p4QVpeIb7SkLUFmbWLjj4XXMs+rVeVGNHyn+3b2sJS6+N0DChx+/zE6BwDQCng0lTRlgy/SIczGBW8njsXTaUiInOSMpI2JHl+csWyWAFYX2pE5Ed4P+rliWBiBaRJAG19AlOntKvCSQDy+d4nxQnQDtC7wtjPoJreAbZrjSMz6GAPuLfKAk4gTZ+AKfWmoFsTfgE0V7IDIKeBAJpAlkZ9EsCAngNkODT99NNPz2W8N0/iQTbiY6JQRbejYqQPgPgKHdI5hKy/AdA2ctcC3vPgNyD2+inWhYqgTwBxfOm7rHMK8ROQb/gxGCrBvAMUxzO1bxrXnZ8ArG/b8E4pTkHtAcgJYL9/wDv5DALgEs7+YtFNJeOrZ3ge3Vp7WEtoLvyD6LPPuL6zlFJvJxMsJTz1DwTA+yWzdlMIhWF6+YdHQFf+ARWfGeFSa/xIb2e16vAPMC9JZISDNWSXf0CuJVFo2D9I60w88A9U8dm7R/4B5vi/9A/0U/8g7kQD/kEsEl9cp39wDhjwD1IUOXroH9j/3j/Q0zP/QDnUD/wD46KLatw/wMg7nmH/AB0r4ph/oJVbDu9G/YMaykJaN5r6pn+wjr7RAdHJTF74BwKIbtvXGURUtvnB3f4BOvEPsCrJoW/7B6ol0A494B8krKJJAHf9A45XkwDu+geGASCAu/4BcLyZBNDnH4gUA7QAbvsHrYQCuO0ftBIK4L5/IH0igBH/QACjklYeFxnz8w9++umR5P6TGQ6Wjfi4yFfR7SjnthhgAMhasouli/dF7wFih3QNB3jPg98A125Fd2jZedEOIF8qiH2XdU7BfQLK3O/HIKeQ3gGm3Y3vky4VEO0eYJfvA+h+/ypnr/YA5ATUJLrYlMVKKCCNBKEmkKVQJ/0jK9S0llL71sqOE0gvoypkOrtNpLTMY9HbyQRLCTfzn72AfHmjKnsfxT+oTbiWUPmZ5Y9vlTUEtcZfHmVIpj3Oi4I5vdVYERH1d35LUePcdGrKEZaGOPwPKbRwr65vWLqjBH3LHu2Ff1ATKHgKiDB+09aEuaS+c8GeoOlv9g+S22nEP3B7YZ9/cAqIOOIfOBH+/AMwqB74BzZhlRn2D0hxvDOj/oHCRWrQPzDYlOyYf4BN29e30l3/QLXsSbZoN/0DJdnLJvGOfwAKk943PT3wDwwHPPAPSFWN+gdiFgz6B3K4Qf9ABjziH0gJh/0DKfq4f8C1f+QfQEo//+Cnf1t/AFGZQwaktm6jAAAAAElFTkSuQmCC"
	}
	window._rhtmlSpinnerSrc = uri;
	window._rhtmlSpinnerTimeout = setTimeout(
			function() {
				window._rhtmlSpinnerTimeout = undefined;
				var outer = document.createElement("div"), inner = document
						.createElement("div"), outS = outer.style, inS = inner.style, inH = size
						* (go12 ? 12 : 3);
				outer.className = inner.className = "escapes-gpu-disabled";
				outer.style.cssText = "z-index:90; position:absolute; left:50%; top:50%;margin-left:"
						+ Math.round(size / -2)
						+ "px; margin-top:"
						+ Math.round(size / -2)
						+ "px;width:"
						+ size
						+ "px; height:" + size + "px; overflow:hidden;";
				inner.style.cssText = "position:absolute; left:0px; top:0px;width:"
						+ size
						+ "px; height:"
						+ inH
						+ "px;background-image: url("
						+ uri
						+ ");background-size:" + size + "px " + inH + "px;";
				outer.appendChild(inner);
				document.body.appendChild(outer);
				var spinerval = setInterval(
						go12 ? function() {
							inS.top = -(pos = (pos + 1) % 12) * size + "px"
						}
								: function() {
									pos = (pos + 1) % 12;
									inS.top = -(pos % 3) * size + "px";
									outS.webkitTransform = outS.msTransform = outS.MozTransform = outS.OTransform = outS.transform = "rotate("
											+ (Math.floor(pos / 3) * 90)
											+ "deg)"
								}, 56);
				window._removeRHTMLSpinner = function() {
					function stop() {
						clearInterval(spinerval);
						if (outer.parentNode) {
							outer.parentNode.removeChild(outer)
						}
					}
					if (window.SC && window.CW && CW.Anim) {
						CW.Anim.Fader.create({
							element : outer,
							from : 1,
							to : 0,
							duration : 350,
							stopSpin : stop.listens("finish")
						}).start()
					} else {
						stop()
					}
				}
			}, 500);
	window._removeRHTMLSpinner = function() {
		if (window._rhtmlSpinnerTimeout) {
			clearTimeout(window._rhtmlSpinnerTimeout)
		}
		window._rhtmlSpinnerTimeout = undefined
	}
})();