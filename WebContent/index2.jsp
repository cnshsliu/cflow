<html> <head>
	<title>Workflow Engine as a Service</title>
<meta content="Workflow, BPM, PaaS Service, Cloud Service, Process Management, Business Process, MyWorldFlow" name="keywords">
<meta content="MyWordflow is a PaaS (Platform as a Service) cloud service, provides Workflow Engine as a Service to developers, MyWorldFlow can be used in BPM" name="description">
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<LINK href="/cflow/rsstyle.css" rel="stylesheet" type="text/css">
	<link rel="stylesheet" href="css/slideshow/style.css" />
</head> <body>
 <center>
     <table style="width:800px;">
        <tr>
          <td valign="top" align="left"><a href="/cflow" border="0"><img src="images/logo3764.png" alt="My World Flow Alpha" border="0"></a>Alpha</td>
          <td algin=center class="menu">
	  <a href="reg.jsp">Sign up</a> | <a href="login.jsp">Sign in </a>
          </td>
    	<td align="right">
	   <div id='devConsole'>Developer Console</div>
    	</td>
        </tr>
    </table> 
    <table style="width:100%; background-color: #9FC;"><!--  background: #99FFCC url(/cflow/images/ctf2.png) bottom right no-repeat--><tr><td align="center">
    <table style="width:800px; background-color: #9FC; font-size:18px;">
  <tr><td>
  <DIV id='intro'>
	  <h1>这是一个云流程运行服务</h1> 专门帮助 <B><font color=red>应用开发者</font></B> 快速实现业务应用中的流程功能。<p> 大部分的行业应用都有流程，需要向工作人员分配工作，或者调度计算机应用系统的运行，协同是行业应用的核心应用场景之一。使用流程云服务，开发者无须购买昂贵的流程软件，也无须自行编码实现复杂的流程逻辑，只须用所见即所得的拖拉方式将流程画出来，就可交由流程云服务去驱动流程的运行。开发者使用API, 与云流程服务交互，例如取得某个操作者的工作列表，或者向云流程服务提交一项工作完成。云流程服务也支持在流程执行过程中直接调用其他应用，方便开发者实现基于流程的应用集成。</p>
  </DIV>
  </td><td align=center>
  <center>
		<img src="images/slideshow/ssarch.png" alt="MWF" title="MyWorldFlow" />
	</center>
  </td></tr></table>
  <i>本站面向<a href="http://www.google.com/chrome">Chrome browser</a>进行了优化，IE可能存在支持不好的情况</i>
   <table style="width:800px; background-color: #3CF; font-size:18px; color: #000066;">
  <tr><td align=left id="tonstext">
		  <div class='desc'>在行业应用中，往往离不开流程功能，而流程功能的实现，要么购买昂贵的专有流程套件，要么使用复杂的开发框架来自行开发，不仅投入的资金和定制开发工作量大，而且，流程逻辑的实现一旦固定到代码里，就会失去灵活性，甚至会直接影响业务开展的效率。MyWorldFlow云流程服务简单易用、支持大规模流程运行、灵活方便。对开发者来说，使用云流程服务，无须任何的安装、配置工作，它的使用方式就是如下这么简单： <DIV id='design2run'>第一步: 拖拉方式画出流程; 第二步: 交由云流程服务运行流程</DIV>
		  <dl>
			  <dt><b>也请按照以下步骤来试用云流程运行服务:</b></dt>
			  <ol>
				  <li>首先请下载并阅读 <a href="/cflow/tutorial/MWF_Intro_for_Developer.ppsx">云流程介绍材料</a>, 了解一下这项服务的基本情况</li>
				  <li>点击本页上部的l"Sin Up"连接, <a href="/cflow/reg.jsp">注册开发者ID</a>.</li>
				  <li>开发者ID注册成功后，有一些工作流模板会自动复制给您，您可以逐个打开，看看它们是干什么的</li>
				  <li>接下来，您可以使用图形方式的流程设计器来设计自己的工作流模板</li>
				  <li>在"Testbed"中，您可以启动一个流程，对其进行测试。</li>
				  <li>使用云流程服务API, 在您自己的应用中嵌入流程功能。 云流程服务开发者控制台（本站）的功能也是用云流程服务API开发的，您可以查看里面的页面的网页源代码，学习如何使用JavaScript来调用云流程服务的功能，并与网页页面进行结合；下面提供下载的API中也有丰富的例子，比如，云流程服务所使用的单元测试代码展示了如何完全用代码来与云流程服务交互</li>
		  </ol>
			  <dt><b>API and SDK</b></dt>
			  <ol><li><a href="/cflow/tutorial/cflowClient.7z">下载云流程服务SDK</a> (Java Wrapper of Restful API). 
		  		</ol>

		  <dt><b>与其它应用双向集成</b></dt>
		  <dd>开发者可以在应用程序中调用云流程服务， (<a href="/cflow/tutorial/StartWorkflow.java">这是一个启动流程的例子</a>)； 也可以为通过为外部应用编写适配器的方式，由流程引擎来驱动外部应用的执行，云流程服务会将流程的上下文书局传递给外部应用的适配器，外部应用适配器也可向流程返回上下文数据。·<a href="/cflow/tutorial/TestScriptWeb.java">例子代码</a></dd>
		  <dt><b>尽量使用角色而不是固定用户名</b></dt>
		  <dd>在流程设计时，将工作项分配给角色，比分配给固定的人要灵活得多。通过在云流程服务中定义“团队”, 云流程服务运行时，会在团队中查找角色的对应关系，并最终将工作分配给具体的人。 </dd>
		 </dl>
	 <div class='desc'>当前的流程云服务为好友预览版，在界面上可能不好看，但功能上非常完整。渴望您的意见和建议，这些对我们非常重要, 请随时通过电话、邮件或者微博向我们反馈。</div>
		  <div>(下面的很多链接的内容已过时，会尽快修改) </div>
			  	
  </td></tr></table>
		 
   <table id='tad'>
  <TR>
  <TD width='33%' valign="top">
 	<div class='pheader'>Tutorial</div>
    <div class='pbody'><ui>
    <li><a href="tutorial/quickstart.html">Quick Start Guide</a>
    <li><a href="tutorial/how_tos.html">How to</a>
    <li><a href="tutorial/videos.htm">Videos</a>
      </ul>
    </div>
  </TD>
  <TD width='33%' valign="top">
 	<div class='pheader'>API</div>
    <div class='pbody'><ui>
    <li><a href="tutorial/mwf_api.html">Restuful API</a>
    <li><a href="tutorial/sdk.html">Java SDK</a>
    <li><a href="tutorial/programming_with_mwf_sdk.html">Example codes</a>
    </ul><p class='memo'>MWF account is required to call thoese APIs<br>
 (<a href="reg.jsp">sign up</a> here).</p></div>
  </TD>
  <TD width='33%' valign="top">
 	<div class='pheader'>Demo</div>
    <div class='pbody'><a href="login.jsp">MyWorldFlow demo site</a> shows MSF's features and demonstrates MWF integration to developers
    </div>
  </TD>
  </TR>
  </table>
  </td></tr></table>
  <table id='support'>
  <tr><td>
	<p><a href="https://twitter.com/intent/tweet?screen_name=MWFSupport" class="twitter-mention-button" data-lang="zh-cn" data-related="MWFSupport"><img src="/cflow/images/twitter.png" border="0"></a>
<p>  You may also post your technical support request to <a href="mailto:myworldflow@yahoogroups.com">myworldflow@yahoogroups.com</a></p>
<p>  For MyWorldFlow technical discussion,  <a href="http://groups.yahoo.com/group/myworldflow">please go to MWF Yahoo! Groups myworldflow</a></p>

</td></tr>
  </table>

<div id="footer"><BR><BR>
      Copyright &#169; 2012 The MyWorldFlow.
    </div> 
 </center>
		<script src="javascript/jquery/1.7.1/jquery.min.js"></script>
		<script src="javascript/slideshow/js/jquery.rs.slideshow.lkh.js"></script>
		<script src="javascript/slideshow/js/dynamic-controls-bootstrap.js"></script>
  
</body>

</html>



