var canvas;
var stage;
var bgSrc=new Image();
var bg;
var btnSrc=new Image();
var btn;

var centerX = 275;
var centerY = 150;
var gfxLoaded = 0;

function Main()
{
	canvas = document.getElementById('HelloWorld');
	stage = new Stage(canvas);
	stage.mouseEventsEnabled = true;
	
	/*Load GFX*/
	bgSrc.src = 'bg.png';
	bgSrc.name = 'bg';
	bgSrc.onload = loadGfx;
	
	btnSrc.src = 'button.png';
	btnSrc.name = "button";
	btnSrc.onload = loadGfx;
	
	Ticker.setFPS(30);
	Ticker.addListener(stage);
}

function loadGfx(e)
{
	if(e.target.name='bg') {bg = new Bitmap(bgSrc);}
	if(e.target.name='button'){btn = new Bitmap(btnSrc);}
	gfxLoaded ++;
	if(gfxLoaded == 2)
	{
		buildInterface();
	}
}

function buildInterface()
{
	btn.x = centerX - 40;
	btn.y = centerY - 12;
	stage.addChild(bg, btn);
	stage.update();
	
	btn.onPress = showText;
}

function showText()
{
	console.log('This works like trace!');
	btn.onPress = null;
	
	var msg = new Text('Hello World!', 'Bold 25px Arial', '#EEE');
	
	msg.x = centerX - 70;
	msg.y = centerY;
	
	stage.addChild(msg);
	msg.alpha = 0;
	
	
	Tween.get(btn).to({y:centerY + 50}, 300);
	Tween.get(msg).wait(400).to({alpha:1}, 400);
}