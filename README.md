# 简易版知乎日报的实现

该应用实现如下功能：</br>
1 知乎日报当日新闻的展示（app首页仅使用ListView展示图片及标题）</br>
2 一条新闻的详细情况的展示（使用Webview结合CSS进行展示）</br>
3 单条新闻的收藏</br>
4 收藏夹的查看</br></br>

应用实现过程中出现的问题：</br>
1 如何在获取到服务器的数据之后进行解析，并且更新UI</br>
服务器上的数据获取需要开启新的线程，避免在主线程中操作，导致ANR的出现，影响用户体验。而worker thread中无法更新UI，该应用采用runOnUiThread的方式，在数据获取完之后回调HttpCallBackListener的onFinish()方法，在onFinish()方法中对获取的JSON数据进行解析，解析完之后调用runOnUiThread方法更新UI</br>

2 ListView无法正常显示的问题</br>
ListView如果无法正常显示可以从两个方面下手找问题，第一，布局文件，看布局文件中的每个控件的属性设置；第二，adpter的数据传递，可用断点调试参看</br>
