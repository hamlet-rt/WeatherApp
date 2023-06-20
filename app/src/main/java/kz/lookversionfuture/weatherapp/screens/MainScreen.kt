package kz.lookversionfuture.weatherapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import kz.lookversionfuture.weatherapp.MainList
import kz.lookversionfuture.weatherapp.R
import kz.lookversionfuture.weatherapp.data.WeatherModel
import kz.lookversionfuture.weatherapp.ui.theme.BlueLight
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun MainCard(currentDay: MutableState<WeatherModel>, onClickSync: () -> Unit, onClickSearch: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(5.dp),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(0.9f),
            elevation =  0.dp,
            shape = RoundedCornerShape(10.dp),
            backgroundColor = BlueLight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                        ){
                    Text(
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                        text = currentDay.value.time,
                        style = TextStyle(fontSize = 15.sp),
                        color = Color.White
                    )
                    AsyncImage(
                        model = "https:${currentDay.value.icon}",
                        contentDescription = "im1",
                    modifier = Modifier
                        .size(35.dp)
                        .padding(end = 8.dp, top = 2.dp))
                }
                Text(
                    text = currentDay.value.city,
                    style = TextStyle(fontSize = 24.sp),
                    color = Color.White
                )
                Text(
                    text = if (currentDay.value.currentTemp.isNotEmpty()) {
                        "${currentDay.value.currentTemp.toFloat().toInt()}°C"
                    } else {
                        "${currentDay.value.minTemp.toFloat().toInt()}°C" +
                                " / ${currentDay.value.maxTemp.toFloat().toInt()}°C"
                    },
                    style = TextStyle(fontSize = 65.sp),
                    color = Color.White
                )
                Text(
                    text = currentDay.value.condition,
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.White
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    IconButton(onClick = {
                        onClickSearch.invoke()
                    }) {
                        Icon(painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "im2",
                            tint = Color.White)
                    }
                    Text(
                        text = "${currentDay.value.minTemp.toFloat().toInt()}°C" +
                                    " / ${currentDay.value.maxTemp.toFloat().toInt()}°C",
                        style = TextStyle(fontSize = 16.sp),
                        color = Color.White,
                        modifier = Modifier
                            .padding(top = 9.dp)
                    )
                    IconButton(onClick = {
                        onClickSync.invoke()
                    }) {
                        Icon(painter = painterResource(id = R.drawable.ic_sync),
                            contentDescription = "im3",
                            tint = Color.White
                            )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayout(daysList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>){
    val tabList = listOf("HOURS", "DAYS")
    val pagerState = rememberPagerState()
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier
        .padding(start = 5.dp, end = 5.dp)
        .clip(RoundedCornerShape(5.dp))
        .alpha(0.8f)
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            backgroundColor = BlueLight,
            contentColor = Color.White,
            indicator = {position ->
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(pagerState, position)
                        )
            },
        ) {
            tabList.forEachIndexed{index, text ->
                Tab(modifier = Modifier
                    .clip(RoundedCornerShape(5.dp)),
                    selected = false, onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                    text = {
                        Text(text = text, style = TextStyle(color = Color.White))
                    }
                )
            }
        }
        HorizontalPager(
            count = tabList.size,
            state = pagerState,
            modifier = Modifier
                .weight(1.0f)
            ) {index ->
            val list = when(index){
                0 -> getWeatherByHours(currentDay.value.hours)
                1 -> daysList.value
                else -> daysList.value
            }
                MainList(list, currentDay)
        }
    }
}


private fun getWeatherByHours(hours: String): List<WeatherModel>{
    if(hours.isEmpty()) return listOf()

    val hoursArray = JSONArray(hours)
    val list = ArrayList<WeatherModel>()
    for (i in hours.indices){
        if (hoursArray.length() <= i) {
            break
        }
        val item = hoursArray[i] as JSONObject
        list.add(
            WeatherModel(
                "",
                    item.getString("time"),
                item.getString("temp_c").toFloat().toInt().toString() + "°C",
                item.getJSONObject("condition").getString("text"),
                item.getJSONObject("condition").getString("icon"),
                "",
                "",
                "",
            )
        )
    }
    return list
}