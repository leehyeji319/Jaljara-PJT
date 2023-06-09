package com.ssafy.jaljara.ui.screen.child

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.twotone.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.jaljara.R
import com.ssafy.jaljara.ui.screen.StarLink
import com.ssafy.jaljara.ui.theme.DarkNavy
import com.ssafy.jaljara.ui.vm.ChildViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import com.ssafy.jaljara.ui.vm.ContentsViewModel


/**
 * 자녀 앱의 시작점입니다.
 * composable routing 및
 * 공통 bar가 사용됩니다.
 * */

enum class ChildScreen(@StringRes val title: Int, val url: String) {
    Start(title = R.string.main, "/main"),
    StarLink(title = R.string.star_link, "/starlink"),
    Contents(title = R.string.contents, "/contents"),
    Coupon(title = R.string.coupon, "/coupons"),
    Mission(title = R.string.mission, "/mission"),
    ContentsDetail(title = R.string.content, "/content")
}


private const val TAG = "ChildApp"

data class NavigationInfo(val route: ChildScreen, val icon: ImageVector)
@Composable
fun ChildNavigationBar(
    /** 원하는 아이콘을 찾아서 넣으세요 **/
    items : List<NavigationInfo> = listOf(
        NavigationInfo(ChildScreen.Start, Icons.TwoTone.Home),
        NavigationInfo(ChildScreen.StarLink, Icons.TwoTone.Insights),
        NavigationInfo(ChildScreen.Contents, Icons.TwoTone.Explore),
        NavigationInfo(ChildScreen.Coupon, Icons.TwoTone.Redeem),
        NavigationInfo(ChildScreen.Mission, Icons.TwoTone.Rule)
    ),
    navController : NavController,
    selectedItem : Int,
    onChangeNavIdx: (Int) -> Unit = {},
) {
    NavigationBar(
        modifier = Modifier.clip(shape = RoundedCornerShape(
            topStart = CornerSize(50),
            topEnd = CornerSize(50),
            bottomEnd = CornerSize(0),
            bottomStart = CornerSize(0)
        )),
        containerColor = DarkNavy
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.route.name) },
                label = { Text(stringResource(id = item.route.title), style = MaterialTheme.typography.titleSmall) },
                selected = selectedItem == index,
                onClick = {
                    navController.navigate(item.route.url){
                        popUpTo(ChildScreen.Start.url)
                    }
                    onChangeNavIdx(index)
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildApp(
    viewModel: ChildViewModel = viewModel(),
    viewModelContents: ContentsViewModel = viewModel(),
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val activity = LocalContext.current as ComponentActivity

    // userId
    var userId = viewModel.test!!.userInfo.userId.toLong()

    // 하단 네비게이션 선택 애니메이션 용
    var navBarSelectedItem by rememberSaveable { mutableStateOf(0) }

    val sleepInfo = viewModel.childSleepResponse
    viewModel.getChildSleepInfo(userId)

    setAlarm(LocalContext.current, sleepInfo.targetBedTime)

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            if(uiState.showNavigation){ChildNavigationBar(
                navController = navController,
                selectedItem = navBarSelectedItem,
                onChangeNavIdx = {
                    navBarSelectedItem = it
                }
            )}
        },
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()
        val _uiState by viewModelContents._contentsListUiState.collectAsState()
        NavHost(
            navController = navController,
            startDestination = ChildScreen.Start.url,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = ChildScreen.Start.url) {

                viewModel.setNavShow(true)

                ChildMainView(viewModel,viewModelContents,
                    onClickMission = {
                        navController.navigate(ChildScreen.Mission.url)
                        navBarSelectedItem = 4
                    },
                    onClickCoupon ={
                        navController.navigate(ChildScreen.StarLink.url)
                    },
                    onClickContent = {
                        navController.navigate(ChildScreen.ContentsDetail.url)
                    },
                    userId = userId
                )

                navBarSelectedItem = 0
            }
            composable(route = ChildScreen.StarLink.url) {

                viewModel.setNavShow(true)

                StarLink(viewModel, userId)

                navBarSelectedItem = 1
            }
            composable(route = ChildScreen.Contents.url) {

                viewModel.setNavShow(true)

                ContentsView(viewModelContents,
                    onClickContents = {
                        navController.navigate(ChildScreen.ContentsDetail.url)
                    }
                )

                navBarSelectedItem = 2
            }
            composable(route = ChildScreen.Coupon.url) {
                viewModel.setNavShow(true)
                // 쿠폰 함수
                CouponScreen(viewModel, userId=userId)
                navBarSelectedItem = 3
            }
            composable(route = ChildScreen.Mission.url) {
                viewModel.setNavShow(true)
                // 미션 함수
                ChildMission(viewModel, userId=userId)
                navBarSelectedItem = 4
            }
            composable(route = ChildScreen.ContentsDetail.url) {
                viewModel.setNavShow(true)
                ContentsDetailView(viewModelContents)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnspecifiedImmutableFlag")
fun setAlarm(context: Context, targetBedTime:String){
    if(targetBedTime != ""){
        //    val timeSec = System.currentTimeMillis() + 5000
//            val temp = "11:09"
        Log.d("설정 수면 시간", targetBedTime)
        val now = LocalDateTime.now()
        val timeArr = targetBedTime.split(":")
        val hour = timeArr[0]
        val minute = timeArr[1]
        var temptime = LocalDateTime.of(now.year, now.monthValue, now.dayOfMonth, hour.toInt(), minute.toInt(), 0)
        if(now.isAfter(temptime)) temptime = temptime.plusDays(1)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ChildAlarm::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context,0,intent, PendingIntent.FLAG_MUTABLE)
//        alarmManager.set(AlarmManager.RTC_WAKEUP, temptime.atZone(ZoneId.systemDefault()).toInstant()?.toEpochMilli() ?: 0, pendingIntent)
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(temptime.atZone(ZoneId.systemDefault()).toInstant()?.toEpochMilli() ?: 0, pendingIntent), pendingIntent);
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview(showSystemUi = true)
fun preview(){
    ChildApp()
}

