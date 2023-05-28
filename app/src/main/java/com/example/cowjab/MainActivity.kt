package com.example.cowjab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cowjab.ui.theme.CowJabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CowJabTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TopLevel()
                }
            }
        }
    }
}

@Composable
fun TopLevel(core: CoreViewModel = CoreViewModel()) {
    // Instantiate the core and inject it into the UI code.
    MainScreen(core)
}

@Composable
fun MainScreen(core: CoreViewModel) {

    var tabState by remember { mutableStateOf(0) }

    val onTabStateChange = { newTabState: Int -> tabState = newTabState}

    Column() {
        TabRow(selectedTabIndex = tabState) {
            Tab(selected = tabState == 0, onClick = { tabState = 0 },
                text = { Text("Bulls")})
            Tab(selected = tabState == 1, onClick = { tabState = 1 },
                text = { Text("Farms") })
            Tab(selected = tabState == 2, onClick = { tabState = 2 },
                text = { Text(core.currentFarmName) })
        }

        when (tabState) {
            0 -> BullsPage(core = core)
            1 -> FarmsPage(core = core, onTabStateChange = onTabStateChange )
            2 -> FarmPage(core = core)
        }
    }
}

//////////////////////////////////////////////////////////////////

@Composable
fun FarmsPage(core: CoreViewModel, onTabStateChange: (Int) -> Unit) {

    val openAddFarm = remember { mutableStateOf(false) }

    if (openAddFarm.value) {
        AddFarm(
            openAddFarm = openAddFarm,
            addFarm = {
                    customerCode: String, farmName: String, farmAddress: String -> core.addFarm(
                customerCode, farmName, farmAddress,
            )
            })
    }

    else {
        Button(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            onClick = { openAddFarm.value = true }
        ) { Text(text = "Add Farm")}
    }

    val farms = core.farms

    Column {
        farms.forEach {
                farm -> FarmItem(farm = farm, core = core, onTabStateChange = onTabStateChange)
        }
    }

// // Floating Dialog Box code.  I didn't like it: too hard to read.

//    val openDialog = remember { mutableStateOf(false) }
//    val dialogWidth = 200.dp
//    val dialogHeight = 100.dp
//
//    if (openDialog.value) {
//        var farmName by rememberSaveable { mutableStateOf("") }
//        var farmAddress by rememberSaveable { mutableStateOf("") }
//
//        Dialog(onDismissRequest = { openDialog.value = false }) {
//            Column {
//                TextField(
//                    value = farmName,
//                    onValueChange = { farmName = it },
//                    label = { Text(text = "Farm Name")}
//                )
//                TextField(
//                    value = farmAddress,
//                    onValueChange = { farmAddress = it },
//                    label = { Text(text = "Farm Address")}
//                )
//                Button(onClick = {
//                    core.addFarm(name = farmName, address = farmAddress)
//                    openDialog.value = false
//                })
//                { Text(text = "Save Farm") }
//            }
//        }
//    }

}

@Composable
fun AddFarm(openAddFarm: MutableState<Boolean>, addFarm: (String, String, String) -> Unit) {

    var customerCode by rememberSaveable { mutableStateOf("") }
    var farmName by rememberSaveable { mutableStateOf("") }
    var farmAddress by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        TextField(
            modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth(),
            value = customerCode,
            onValueChange = { customerCode = it },
            label = { Text(text = "Customer Code")}
        )
        TextField(
            modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth(),
            value = farmName,
            onValueChange = { farmName = it },
            label = { Text(text = "Farm Name")}
        )
        TextField(
            modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth(),
            value = farmAddress,
            onValueChange = { farmAddress = it },
            label = { Text(text = "Farm Address")}
        )
        Row {
            Button(
                modifier = Modifier.padding(4.dp),
                onClick = {
                    addFarm(customerCode, farmName, farmAddress)
                    openAddFarm.value = false
                })
            { Text(text = "Save Farm") }
            Button(
                modifier = Modifier.padding(4.dp),
                onClick = {
                    openAddFarm.value = false
                })
            { Text(text = "Cancel") }
        }
    }
}


@Composable
fun FarmItem(farm: FarmModel, core: CoreViewModel, onTabStateChange: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        elevation = 4.dp,
        border = BorderStroke(width = 1.dp, color = Color.LightGray),
        backgroundColor = Color(red = 0.8f, green = 1f, blue = 0.8f),
        ) {
        Row(modifier = Modifier.padding(vertical = 4.dp)){
            Button(
                modifier = Modifier.padding(horizontal = 8.dp).align(Alignment.CenterVertically),
                onClick = {
                    core.selectFarm(farmID = farm.farmID)
                    onTabStateChange(2)
                }) {
                Text(text = "Select")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = farm.customerCode)
                Text(text = farm.name)
                Text(text = farm.address, color = Color.Blue)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview(core: CoreViewModel = CoreViewModel()) {
    CowJabTheme {
        MainScreen(core)
    }
}



//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            CowJabTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colors.background
//                ) {
//                    Greeting("Android")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun Greeting(name: String) {
//    Text(text = "Hello $name!")
//}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    CowJabTheme {
//        Greeting("Android")
//    }
//}