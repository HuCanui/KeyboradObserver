> **使用：** 		1、在Application的onCreate的时候

```
       KeyboardVisibilityObserver.getInstance().init(this);
```
		

> 2、EventBus开源库加下

```
       implementation 'org.greenrobot:eventbus:3.1.1'
```
		
	

> 	3、在需要监听键盘的事件的类里面注册/反注册下EventBus

```
       EventBus.getDefault().register(this);
       EventBus.getDefault().unregister(this);
```
		

> 4、键盘事件监听下

		

```
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(KeyboardVisibleEvent event){
        Toast.makeText(this, event.isVisible ? "键盘弹起" : "键盘收起", Toast.LENGTH_SHORT).show();
    }
```
> 详解博客地址：(https://blog.csdn.net/u014798175/article/details/84108506)
