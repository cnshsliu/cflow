// these vars are used as masks
var _ROUND_UPPER = 1
var _ROUND_LOWER = 2;

document.getElementsByClassName = function(className, parentElement) {
  var children = ($(parentElement) || document.body).getElementsByTagName('*');
  return $A(children).inject([], function(elements, child) {
    if (child.className.match(new RegExp("(^|\\s)" + className + "(\\s|$)")))
      elements.push(child);
    return elements;
  });
}

function DomCheck(){
return(document.createElement && document.getElementById)
}

function DomCorners(cls,img,size){
	roundCorners(cls, img, 3, size);
}

// cls: className; used to select the elements to round
// img: imageName; name of the corner image
// mask: 1, 2, or 3 (1 for top only, 2 for bottom only, 3 for all)
// size: size (in pixels) of a single corner in the corner image
function roundCorners(cls,img,mask,size){
    if (mask == null) mask = 3;
    if (size == null) size = 10;
    var els = document.getElementsByClassName(cls);
    for (var i = 0; i < els.length; i++) {
        var el = els[i];
        var c=new Array(4);
        for(var j=0;j<4;j++){
            c[j]=document.createElement("b");
            c[j].style.display="block";
            c[j].style.height=size+"px";
            c[j].style.fontSize="1px";
            c[j].style.border="none";
            c[j].style.padding="0px";
            c[j].style.margin="0px";
            if(j%2==0)
                c[j].style.background="url("+img+") no-repeat 0 -"+ j*size + "px";
            else
                c[j].style.background="url("+img+") no-repeat 100% -"+ j*size + "px";
        }
        if (mask & _ROUND_UPPER) {
            c[0].appendChild(c[1]);
            el.insertBefore(c[0], el.firstChild);
        }
        if (mask & _ROUND_LOWER) {
            c[2].appendChild(c[3]);
            el.appendChild(c[2]);
        }
        el.style.padding="0";
        el.style.border="none";
    }
}
