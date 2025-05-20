1. install
   ./gradlew assemble
   ./gradlew packageDebugAndroidTest
    adb install C:\Users\fengchenhui\AndroidStudioProjects\TestUiAutomator2\app\build\outputs\apk\debug\app-debug.apk
    adb install C:\Users\fengchenhui\AndroidStudioProjects\TestUiAutomator2\app\build\outputs\apk\androidTest\debug\app-debug-androidTest.apk
2. adb shell
am instrument -w -r -e debug false -e class com.github.uiautomator.stub.Stub com.test.testuiautomator2/androidx.test.runner.AndroidJUnitRunner
am instrument -w -r -e debug false -e class com.xiachufang.task_uiauto.DyTest#testA com.xiachufang.uiauto/androidx.test.runner.AndroidJUnitRunner
3. forward
adb forward tcp:9008 tcp:9008
4. use
curl -X POST -d '{"jsonrpc": "2.0", "id": "1f0f2655716023254ed2b57ba4198815", "method": "ping", "params": {}}' 'http://127.0.0.1:9008/jsonrpc/0'
curl -X POST -d '{"jsonrpc": "2.0", "id": "1f0f2655716023254ed2b57ba4198815", "method": "dumpWindowHierarchy", "params": [true]}' 'http://127.0.0.1:9008/jsonrpc/0'
curl -X POST -d '{"jsonrpc": "2.0", "id": "1f0f2655716023254ed2b57ba4198815", "method": "makeToast", "params": ["HELLO",1000]}' 'http://127.0.0.1:9008/jsonrpc/0'

curl -X POST -d '{"jsonrpc": "2.0", "id": "1f0f2655716023254ed2b57ba4198815", "method": "count", "params": [{"mask": 1, "childOrSibling": [], "childOrSiblingSelector": [], "text": "click me"}]}' 'http://127.0.0.1:9008/jsonrpc/0'

curl -X POST -d '{"jsonrpc": "2.0", "id": "1f0f2655716023254ed2b57ba4198815", "method": "exist", "params": [{"mask": 1, "text": "淘宝"}]}' 'http://127.0.0.1:9008/jsonrpc/0'


curl -X POST \
-H "Content-Type: application/json; charset=UTF-8" \
-d '{"jsonrpc": "2.0", "id": "1f0f2655716023254ed2b57ba4198815", "method": "exist", "params": [{"mask": 1, "text": "淘宝"}]}' \
'http://127.0.0.1:9008/jsonrpc/0'