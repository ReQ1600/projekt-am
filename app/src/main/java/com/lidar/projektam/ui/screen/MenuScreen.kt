package com.lidar.projektam.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lidar.projektam.R
import com.lidar.projektam.data.MenuSource
import com.lidar.projektam.model.MenuItem

@Composable
fun Menu(navController: NavController,
         modifier: Modifier = Modifier
             .fillMaxSize()
             .wrapContentSize(Alignment.Center))
{
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopNavBelt()
        Button(onClick = { navController.navigate("rates") }) {
            Text("Go test")
        }

        val layoutDirection = LocalLayoutDirection.current

        //setting menu item padding and creating the menu item list
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(
                    start = WindowInsets.safeDrawing.asPaddingValues()
                        .calculateStartPadding(layoutDirection),
                    end = WindowInsets.safeDrawing.asPaddingValues()
                        .calculateEndPadding(layoutDirection)
                )
        ) {
            MenuItemList(
                itemList = MenuSource().loadMenuItems(),
                navController = navController
            )
        }

    }
}

@Composable
fun TopNavBelt(modifier: Modifier = Modifier){
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = Color.Gray
    ){
        Box (
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = stringResource(R.string.menu),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
//creating the menu item tile
fun MenuCard(item : MenuItem, navController: NavController, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        //Spendings will be changed to switch later on
                        when(item.index){
                            0 -> navController.navigate("rates")
                            1 -> navController.navigate("transactions")
                            2 -> navController.navigate("charts")
                        }
                    }
                )
            }
    ) {
        Column {
            Image(
                painter = painterResource(item.imageId),
                contentDescription = stringResource(item.titleId),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = LocalContext.current.getString(item.titleId),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineSmall

            )
        }
    }
}

@Composable
fun MenuItemList(itemList: List<MenuItem>, navController: NavController, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(itemList){ item ->
            MenuCard(
                item = item,
                navController = navController,
                modifier = Modifier.padding(8.dp)
            )

        }
    }
}