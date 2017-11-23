# Writing a sample program in Siddhi

## Creating a new Siddhi project

1. Go to **File -> New -> Project**. Select **Siddhi** project type and click **Next** button.

![alt text](images/Figure-1-1.png)

2. In the next step, we are prompted to select a SDK for the project. If you haven't already added a SDK, please 
follow [Setting up Siddhi SDK](../setting-up-siddhi-sdk) documentation to add a new SDK.

   Select the SDK and click **Next** button.

![alt text](images/Figure-1-2.png)

3. Update the project name and the location and click **Next** button.

![alt text](images/Figure-1-3.png)

Now you have successfully created a new **Siddhi** project.

![alt text](images/Figure-1-4.png)

## Creating a new Siddhi file

1. **Right click** on the project and select **New -> Siddhi File**.

![alt text](images/Figure-2-1.png)

2. Enter the file name and click **OK** button. This will create a new file 
with the default app annotation.

![alt text](images/Figure-2-2.png)

Now you should have a new Siddhi file with the default app annotation.

![alt text](images/Figure-2-3.png)

## Running Siddhi files

1. Write a sample siddhi program as shown in below.

![alt text](images/Figure-3-1.png)

2. Write a event input file(input.txt) as shown in below.

![alt text](images/Figure-3-2.png)

**NOTE:** You don't need to define an input.txt file, if you are getting events through an extension.   

3. Select **Edit Configurations** from the toolbar.

![alt text](images/Figure-3-3.png)

4. Click **+** button and select **Siddhi** to add a siddhi ***run/debug configuration***.

![alt text](images/Figure-3-4.png)

5. Provide a name for the configuration and select the siddhi file from the file chooser.  

![alt text](images/Figure-3-5.png)

6. Select the event input file from the file chooser and click **OK**.  

![alt text](images/Figure-3-6.png)

**NOTE:** You don't need to select an input.txt file, if you are getting events through an extension. 

Near the main function, in the gutter area, **Run** icon can be found.

7. Select **Run** command. This will run the Siddhi file. 

![alt text](images/Figure-3-7.png)

8. The output will be shown in the **Run** window.

![alt text](images/Figure-3-8.png)

## Debugging Siddhi files

* Use the above provided(described under **Running Siddhi files**) sample siddhi file and event input file for this as 
well.

**NOTE:** You don't need to define an input.txt file, if you are getting events through an extension.  

* Provide a run/debug configuration (described under **Running Siddhi files** step 3-6)  if you haven't configured yet.

1. Add breakpoints as shown in below and select **Debug** command. This will start the debug process.

![alt text](images/Figure-3-9.png)

**NOTE** Siddhi only allows to add breakpoints on the beginning of a query input section or on the beginning of a 
query output. 

2. The output will be shown in the **Debug** window.

![alt text](images/Figure-3-10.png)
