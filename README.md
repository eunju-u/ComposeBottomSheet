# DraggableBottomSheet
<img src = "https://github.com/eunju-u/DraggableBottomSheet/assets/55544506/a3af6a8c-d68f-4339-bbc9-75072e4f92d6" width="40%" height="40%"> ..... <img src = "https://github.com/eunju-u/DraggableBottomSheet/assets/55544506/eb36df65-9542-468f-b0b4-e845fca0a137" width="40%" height="40%"> 


# Description

In Android Compose, when using the ModalBottomSheet provided by the framework,

If there are two views within a Column with verticalArrangement = Arrangement.SpaceBetween, 

the overall view size is not fixed, which resulted in the inability to render the desired view.

I have resolved a solution for this and implemented a draggable sheet that is functional from the left, right, and bottom.  



+ The drag function works as follows:

> The sheet is displayed in both full and half sizes.

> When you drag downward from the full-size sheet, it transitions into a half-size view, 

> and when you drag downward from the half-size sheet, it disappears. 

> Dragging upward reverses these actions. 

> The background color, size, or offset of the displayed sheet changes based on the upward and downward movements.
  
# Getting Started / Installation

Step 1. Add the JitPack repository to your build file

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
 
Step 2. Add the dependency

        dependencies {
                implementation 'com.github.eunju-u:DraggableBottomSheet:1.0.7'
        }

# Usage

    var isShowBottomSheet by remember { mutableStateOf(false) }

    BackHandler(enabled = isShowBottomSheet) {
        isShowBottomSheet = false
    }

    if (isShowBottomSheet) {
        DraggableBottomSheet(
            direction = SwipeBottomSheetDirection.BOTTOM,
            surfaceRoundedCornerShape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp
            ),
            onDismiss = {
                isShowBottomSheet = false
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(PaddingValues(start = 20.dp, end = 20.dp, bottom = 15.dp)),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize().background(color = Color.Yellow),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                    }
                }
            })
    }
    
# Project result

BottomSheet


https://github.com/eunju-u/DraggableBottomSheet/assets/55544506/ab7b8893-71f5-4add-926b-912f6a015f83



RightSheet


https://github.com/eunju-u/DraggableBottomSheet/assets/55544506/6d02c021-dfd8-4d9b-89f4-44d32c60ec2e


# License

    Copyright 2022 Eunju Lee
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific langu

