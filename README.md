# Datadog Integration for Hubitat

This Hubitat app allows you to send device states from your Hubitat hub to Datadog. It provides a convenient way to monitor and track the state changes of your Hubitat devices using Datadog's powerful monitoring and analytics platform.

## Features

- Log device states to Datadog at a configurable interval
- Support for a wide range of Hubitat device capabilities
- Ability to select specific devices and attributes to monitor
- Option to log mode events, hub properties, and location properties
- Customizable logging levels for the Hubitat IDE
- Easy integration with Datadog using API keys

## Installation

To install and use the Datadog integration, follow these steps:

1. In the Hubitat web interface, navigate to the "Apps Code" section.
2. Click on the "New App" button.
3. Copy and paste the contents of the `app.groovy` file into the code editor.
4. Click the "Save" button to create the new app.
5. Go to the "Apps" section and click on "Add User App".
6. Select "Datadog Logger" from the list of available apps.
7. Configure the app settings according to your preferences (see the Configuration section below).
8. Click the "Done" button to install the app.

## Configuration

This app provides various configuration options to customize its behavior:

- **Application Name**: Specify a name for the app instance.
- **Datadog API Key**: Enter your Datadog API key to authenticate the connection.
- **IDE Live Logging Level**: Choose the minimum logging level for messages displayed in the Hubitat IDE.
- **Soft-Polling Interval**: Set the interval (in minutes) at which the app will poll device states and send them to Datadog.
- **System Monitoring**: Enable or disable logging of mode events, hub properties, and location properties.
- **Devices to Monitor**: Select the specific devices and attributes you want to monitor. You can either grant access to all attributes or choose individual devices and their corresponding attributes.
- **Event Subscription Options**: Determine whether to log events only when the attribute values change or log all events.

Make sure to configure the Datadog API key correctly to establish a connection between the Hubitat app and your Datadog account.

### Obtaining a Datadog API Key

To obtain a Datadog API key, follow these steps:

1. Log in to your Datadog account.
2. Navigate to the "Integrations" section.
3. Search for "API" and select the "APIs" integration.
4. Click on the "New API Key" button.
5. Give your API key a name and select the appropriate permissions.
6. Click the "Create API Key" button.
7. Copy the generated API key and use it in the app configuration.

## How It Works

The Datadog integration app operates as follows:

1. Upon installation and configuration, the app subscribes to the selected device attributes and events. Events are sent to datadog immediately.
2. At the specified soft-polling interval, the app retrieves the current state of the monitored devices and attributes. This is a soft poll and it does not query devices. The purpose of this is to provide consistency to metrics that don't change often.
3. The app processes the device states and transforms them into a format compatible with Datadog's API.
4. The app sends the formatted data as metrics to Datadog using the provided API key.
5. Datadog receives the metrics and stores them for monitoring, analysis, and visualization purposes.

The app also provides options to log mode events, hub properties, and location properties, giving you a comprehensive view of your Hubitat system in Datadog.

## Troubleshooting

- If the app fails to send metrics to Datadog, ensure that the API key is correctly configured and has the necessary permissions.
- Check the Hubitat IDE logs for any error messages or warnings related to the Datadog integration app.
- Verify that the selected devices and attributes are compatible with the app and are reporting valid state values.

## License

This app is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for more information.

## Credits

The Datadog Integration is developed and maintained by [Logan Garrett](https://github.com/lngarrett). It is a heavily modified fork of [InfluxDB-Logger](https://github.com/HubitatCommunity/InfluxDB-Logger).
