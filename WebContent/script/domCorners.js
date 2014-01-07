//domCorners by Alessandro Fulciniti on web-graphics.com
//read related article


function DomCheck(){
return(document.createElement && document.getElementById)
}

function DomCorners(id,bk,h,tries){
var el=document.getElementById(id);
if(el==null){                              // if the element isn't ready in the DOM...
    if(tries==null) tries=200;
    if(tries>0)                            // and there are still tries...
        setTimeout("DomCorners('"+id+"','"+bk+"',"+h+","+(--tries)+")",50);  // back in 50ms
    return;
    }
var c=new Array(4);

for(var i=0;i<4;i++){                      // create the four elements for rounded corners
    c[i]=document.createElement("b");
    c[i].style.display="block";            // with necessary style declarations
    c[i].style.height=h+"px";
    c[i].style.fontSize="1px";
    c[i].style.border="none";
    c[i].style.padding="0px";
    c[i].style.margin="0px";
    if(i%2==0)
        c[i].style.background="url("+bk+") no-repeat 0 -"+ i*h + "px";
    else
        c[i].style.background="url("+bk+") no-repeat 100% -"+ i*h + "px";
    }
c[0].appendChild(c[1]);
c[2].appendChild(c[3]);
el.style.padding="0";
el.style.border="none";
el.insertBefore(c[0],el.firstChild);       // add top corners
el.appendChild(c[2]);                      // and bottom ones
}
