/*****************************************************************************************************************
 *  Source: https://github.com/lngarrett/hubitat-datadog
 *
 *  Raw Source: https://raw.githubusercontent.com/lngarrett/hubitat-datadog/main/app.groovy
 *
 *  Forked from: https://github.com/HubitatCommunity/InfluxDB-Logger
 *
 *  Description: A SmartApp to log Hubitat device states to Datadog. This app is a heavily modified version of
 *  the original InfluxDB logger.
 *
 *  License:
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *   for the specific language governing permissions and limitations under the License.
 *
 *   Modification History
 *   Date       Name            Change
 *   2024-04-12 Logan Garrett   Initial release
 *   2024-06-02 Logan Garrett   Add support for logging events to Datadog. Add true support for counters.
 *****************************************************************************************************************/

definition(
    name: 'Datadog Integration',
    namespace: 'whiskee',
    author: 'Logan Garrett',
    description: 'Send device states to Datadog',
    category: 'Utility',
    importUrl: 'https://raw.githubusercontent.com/lngarrett/hubitat-datadog/main/app.groovy',
    iconUrl: '',
    iconX2Url: '',
    iconX3Url: '',
    singleThreaded: true
)

preferences {
    page(name: 'setupMain')
    page(name: 'datadogPage')
}

def setupMain() {
    dynamicPage(name: 'setupMain', title: 'Datadog Logger Settings', install: true, uninstall: true) {
        section('') {
            input 'appName', 'text', title: 'Application Name', multiple: false, required: false, submitOnChange: true, defaultValue: app.getLabel()

            href(
                name: 'href',
                title: 'Datadog Settings',
                description : prefApiKey == null ? 'Configure Datadog connection' : 'Datadog API Key set',
                required: true,
                page: 'datadogPage'
            )

            input(
                name: 'configLoggingLevelIDE',
                title: 'IDE Live Logging Level:\nMessages with this level and higher will be logged to the IDE.',
                type: 'enum',
                options: [
                    '0' : 'None',
                    '1' : 'Error',
                    '2' : 'Warning',
                    '3' : 'Info',
                    '4' : 'Debug',
                    '5' : 'Trace'
                ],
                defaultValue: '3',
                displayDuringSetup: true,
                required: false
            )
        }

        section('Polling / Write frequency:') {
            input 'prefSoftPollingInterval', 'number', title:'Soft-Polling interval (minutes)', defaultValue: 10, required: true
        }

        section('System Monitoring:') {
            input 'prefLogModeEvents', 'bool', title:'Log Mode Events?', defaultValue: false, required: true
            input 'prefLogHubProperties', 'bool', title:'Log Hub Properties?', defaultValue: false, required: true
            input 'prefLogLocationProperties', 'bool', title:'Log Location Properties?', defaultValue: false, required: true
        }

        section('Input Format Preference:') {
            input 'accessAllAttributes', 'bool', title:'Get Access To All Attributes?', defaultValue: false, required: true, submitOnChange: true
        }

        if (!accessAllAttributes) {
            section('Devices To Monitor:', hideable:false) {
                input 'accelerometers', 'capability.accelerationSensor', title: 'Accelerometers', multiple: true, required: false
                input 'alarms', 'capability.alarm', title: 'Alarms', multiple: true, required: false
                input 'batteries', 'capability.battery', title: 'Batteries', multiple: true, required: false
                input 'beacons', 'capability.beacon', title: 'Beacons', multiple: true, required: false
                input 'buttons', 'capability.pushableButton', title: 'Pushable Buttons', multiple: true, required: false
                input 'holdableButtons', 'capability.holdableButton', title: 'Holdable Buttons', multiple: true, required: false
                input 'releasableButtons', 'capability.releasableButton', title: 'Releasable Buttons', multiple: true, required: false
                input 'cos', 'capability.carbonMonoxideDetector', title: 'Carbon Monoxide Detectors', multiple: true, required: false
                input 'co2s', 'capability.carbonDioxideMeasurement', title: 'Carbon Dioxide Detectors', multiple: true, required: false
                input 'colors', 'capability.colorControl', title: 'Color Controllers', multiple: true, required: false
                input 'consumables', 'capability.consumable', title: 'Consumables', multiple: true, required: false
                input 'contacts', 'capability.contactSensor', title: 'Contact Sensors', multiple: true, required: false
                input 'doorsControllers', 'capability.doorControl', title: 'Door Controllers', multiple: true, required: false
                input 'energyMeters', 'capability.energyMeter', title: 'Energy Meters', multiple: true, required: false
                input 'humidities', 'capability.relativeHumidityMeasurement', title: 'Humidity Meters', multiple: true, required: false
                input 'illuminances', 'capability.illuminanceMeasurement', title: 'Illuminance Meters', multiple: true, required: false
                input 'locks', 'capability.lock', title: 'Locks', multiple: true, required: false
                input 'motions', 'capability.motionSensor', title: 'Motion Sensors', multiple: true, required: false
                input 'musicPlayers', 'capability.musicPlayer', title: 'Music Players', multiple: true, required: false
                input 'peds', 'capability.stepSensor', title: 'Pedometers', multiple: true, required: false
                input 'phMeters', 'capability.pHMeasurement', title: 'pH Meters', multiple: true, required: false
                input 'powerMeters', 'capability.powerMeter', title: 'Power Meters', multiple: true, required: false
                input 'presences', 'capability.presenceSensor', title: 'Presence Sensors', multiple: true, required: false
                input 'pressures', 'capability.pressureMeasurement', title: 'Pressure Sensors', multiple: true, required: false
                input 'shockSensors', 'capability.shockSensor', title: 'Shock Sensors', multiple: true, required: false
                input 'signalStrengthMeters', 'capability.signalStrength', title: 'Signal Strength Meters', multiple: true, required: false
                input 'sleepSensors', 'capability.sleepSensor', title: 'Sleep Sensors', multiple: true, required: false
                input 'smokeDetectors', 'capability.smokeDetector', title: 'Smoke Detectors', multiple: true, required: false
                input 'soundSensors', 'capability.soundSensor', title: 'Sound Sensors', multiple: true, required: false
                input 'spls', 'capability.soundPressureLevel', title: 'Sound Pressure Level Sensors', multiple: true, required: false
                input 'switches', 'capability.switch', title: 'Switches', multiple: true, required: false
                input 'switchLevels', 'capability.switchLevel', title: 'Switch Levels', multiple: true, required: false
                input 'tamperAlerts', 'capability.tamperAlert', title: 'Tamper Alerts', multiple: true, required: false
                input 'temperatures', 'capability.temperatureMeasurement', title: 'Temperature Sensors', multiple: true, required: false
                input 'thermostats', 'capability.thermostat', title: 'Thermostats', multiple: true, required: false
                input 'threeAxis', 'capability.threeAxis', title: 'Three-axis (Orientation) Sensors', multiple: true, required: false
                input 'touchs', 'capability.touchSensor', title: 'Touch Sensors', multiple: true, required: false
                input 'uvs', 'capability.ultravioletIndex', title: 'UV Sensors', multiple: true, required: false
                input 'valves', 'capability.valve', title: 'Valves', multiple: true, required: false
                input 'volts', 'capability.voltageMeasurement', title: 'Voltage Meters', multiple: true, required: false
                input 'waterSensors', 'capability.waterSensor', title: 'Water Sensors', multiple: true, required: false
                input 'windowShades', 'capability.windowShade', title: 'Window Shades', multiple: true, required: false
            }
        } else {
            section('Devices To Monitor:', hideable:false) {
                input name: 'allDevices', type: 'capability.*', title: 'Selected Devices', multiple: true, required: false, submitOnChange: true
            }
            state.selectedAttr = [:]
            settings.allDevices.each { deviceName ->
                if (deviceName) {
                    deviceId = deviceName.getId()
                    attr = deviceName.getSupportedAttributes().unique()
                    if (attr) {
                        state.options = []
                        index = 0
                        attr.each { at ->
                            state.options[index] = "${at}"
                            index = index + 1
                        }
                        section("$deviceName", hideable: true) {
                            input name:"attrForDev$deviceId", type: 'enum', title: "$deviceName", options: state.options, multiple: true, required: false, submitOnChange: true
                        }
                        state.selectedAttr[deviceId] = settings['attrForDev' + deviceId]
                    }
                }
            }
        }

        section('Event Subscription Options:') {
            input 'filterEvents', 'bool', title:'Only log events when the value changes', defaultValue: true, required: true, submitOnChange: true
        }
    }
}

def datadogPage() {
    dynamicPage(name: 'datadogPage', title: 'Datadog Properties', install: false, uninstall: false) {
        section {
            input 'prefApiKey', 'text', title: 'Datadog API Key', multiple: false, required: true
        }
    }
}

def getDeviceObj(id) {
    def found
    settings.allDevices.each { device ->
        if (device.getId() == id) {
            found = device
        }
    }
    return found
}

def installed() {
    state.installedAt = now()
    state.loggingLevelIDE = 3 // Default to 'Info' on install
    state.metricQueue = []
    updated()
    logger("${app.label}: Installed with settings: ${settings}", 'info')
}

def uninstalled() {
    logger("${app.label}: uninstalled", 'info')
}

def updated() {
    state.loggingLevelIDE = (settings.configLoggingLevelIDE) ? settings.configLoggingLevelIDE.toInteger() : 3
    logger("${app.label}: Updated with settings: ${settings}", 'info')
    logger("Current log level: ${state.loggingLevelIDE}", 'info')

    state.deviceAttributes = []
    state.deviceAttributes << [ devices: 'accelerometers', attributes: ['acceleration']]
    state.deviceAttributes << [ devices: 'alarms', attributes: ['alarm']]
    state.deviceAttributes << [ devices: 'batteries', attributes: ['battery']]
    state.deviceAttributes << [ devices: 'beacons', attributes: ['presence']]
    state.deviceAttributes << [ devices: 'buttons', attributes: ['pushed', 'held', 'released', 'doubleTapped']]
    state.deviceAttributes << [ devices: 'cos', attributes: ['carbonMonoxide']]
    state.deviceAttributes << [ devices: 'co2s', attributes: ['carbonDioxide']]
    state.deviceAttributes << [ devices: 'colors', attributes: ['hue', 'saturation', 'color']]
    state.deviceAttributes << [ devices: 'consumables', attributes: ['consumableStatus']]
    state.deviceAttributes << [ devices: 'contacts', attributes: ['contact']]
    state.deviceAttributes << [ devices: 'doorsControllers', attributes: ['door']]
    state.deviceAttributes << [ devices: 'energyMeters', attributes: ['energy']]
    state.deviceAttributes << [ devices: 'humidities', attributes: ['humidity']]
    state.deviceAttributes << [ devices: 'illuminances', attributes: ['illuminance']]
    state.deviceAttributes << [ devices: 'locks', attributes: ['lock']]
    state.deviceAttributes << [ devices: 'motions', attributes: ['motion']]
    state.deviceAttributes << [ devices: 'musicPlayers', attributes: ['status', 'level', 'trackDescription', 'trackData', 'mute']]
    state.deviceAttributes << [ devices: 'peds', attributes: ['steps', 'goal']]
    state.deviceAttributes << [ devices: 'phMeters', attributes: ['pH']]
    state.deviceAttributes << [ devices: 'powerMeters', attributes: ['power', 'voltage', 'current', 'powerFactor']]
    state.deviceAttributes << [ devices: 'presences', attributes: ['presence']]
    state.deviceAttributes << [ devices: 'pressures', attributes: ['pressure']]
    state.deviceAttributes << [ devices: 'shockSensors', attributes: ['shock']]
    state.deviceAttributes << [ devices: 'signalStrengthMeters', attributes: ['lqi', 'rssi']]
    state.deviceAttributes << [ devices: 'sleepSensors', attributes: ['sleeping']]
    state.deviceAttributes << [ devices: 'smokeDetectors', attributes: ['smoke']]
    state.deviceAttributes << [ devices: 'soundSensors', attributes: ['sound']]
    state.deviceAttributes << [ devices: 'spls', attributes: ['soundPressureLevel']]
    state.deviceAttributes << [ devices: 'switches', attributes: ['switch']]
    state.deviceAttributes << [ devices: 'switchLevels', attributes: ['level']]
    state.deviceAttributes << [ devices: 'tamperAlerts', attributes: ['tamper']]
    state.deviceAttributes << [ devices: 'temperatures', attributes: ['temperature']]
    state.deviceAttributes << [ devices: 'thermostats', attributes: ['temperature', 'heatingSetpoint', 'coolingSetpoint', 'thermostatSetpoint', 'thermostatMode', 'thermostatFanMode', 'thermostatOperatingState', 'thermostatSetpointMode', 'scheduledSetpoint', 'optimisation', 'windowFunction']]
    state.deviceAttributes << [ devices: 'threeAxis', attributes: ['threeAxis']]
    state.deviceAttributes << [ devices: 'touchs', attributes: ['touch']]
    state.deviceAttributes << [ devices: 'uvs', attributes: ['ultravioletIndex']]
    state.deviceAttributes << [ devices: 'valves', attributes: ['contact']]
    state.deviceAttributes << [ devices: 'volts', attributes: ['voltage']]
    state.deviceAttributes << [ devices: 'waterSensors', attributes: ['water']]
    state.deviceAttributes << [ devices: 'windowShades', attributes: ['windowShade']]

    state.softPollingInterval = settings.prefSoftPollingInterval.toInteger()
    manageSchedules()
    manageSubscriptions()
}

def handleModeEvent(evt) {
    logger("handleModeEvent(): Mode changed to: ${evt.value}", 'info')

    def mode = evt.value
    long timestamp = evt.unixTime / 1000

    sendMetricsToDatadog([[name: 'mode', value: mode, timestamp: timestamp]])
}

// These are currently confirmed correct for contact, humidity, level, motion, presence, and temperature. 
// You may need to verify the value strings (e.g. open vs opened) for untested devices, but they are presumed correct.
def handleAttribute(attr, value, unit) {
    def metrics = []

    if (unit) {
        // If unit is present, treat it as a gauge
        try {
            logger("Attr: ${attr} Value: ${value} being handled as gauge", 'debug')
            metrics << [name: attr, value: Float.parseFloat(value), metricType: 'gauge']
        } catch (e) {
            logger("handleAttribute(): Cannot convert ${value} to float. Skipping.", 'warn')
            return null
        }
    } else {
        // If unit is not present, treat it as a count and tag with the value
        logger("Attr: ${attr} Value: ${value} being handled as count", 'debug')
        metrics << [name: attr, value: 1, metricType: 'count', tags: ["value:${value}"]]
    }

    return metrics
}

def handleEvent(evt) {
    def deviceName = evt.displayName
    def deviceId = evt.deviceId
    def deviceType = evt.name
    long timestamp = evt?.unixTime / 1000

    def metrics = handleAttribute(evt.name, evt.value, evt.unit)
    if (metrics != null) {
        sendMetricsToDatadog(metrics.collect { metric ->
            [
                name: metric.name,
                value: metric.value,
                timestamp: timestamp,
                deviceName: deviceName,
                deviceId: deviceId,
                metricType: metric.metricType,
                tags: metric.tags
            ]
        })
    }

    // Send the full event as a log to Datadog
    sendLogToDatadog(evt)
}
def binaryValue(value, active, reverse=false) {
    return reverse ? (value == active ? 0 : 1) : (value == active ? 1 : 0)
}

def softPoll() {
    logger('softPoll()', 'trace')

    logSystemProperties()

    def metrics = []

    if (!accessAllAttributes) {
        def devs
        state.deviceAttributes.each { da ->
            devs = settings."${da.devices}"
            if (devs && (da.attributes)) {
                devs.each { d ->
                    da.attributes.each { attr ->
                        if (d.hasAttribute(attr) && d.currentState(attr)?.value != null) {
                            def unit = d.currentState(attr)?.unit
                            if (unit) {
                                // Only include gauges (attributes with units) in softpoll
                                long timeNow = new Date().time / 1000
                                def metricValues = handleAttribute(attr, d.currentState(attr)?.value, unit)
                                if (metricValues != null) {
                                    metrics.addAll(metricValues.collect { metric ->
                                        [
                                            name: metric.name,
                                            value: metric.value,
                                            deviceId: d.id,
                                            deviceName: d.displayName,
                                            timestamp: timeNow,
                                            metricType: metric.metricType
                                        ]
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        state.selectedAttr.each { entry ->
            d = getDeviceObj(entry.key)
            entry.value.each { attr ->
                if (d.hasAttribute(attr) && d.currentState(attr)?.value != null) {
                    def unit = d.currentState(attr)?.unit
                    if (unit) {
                        // Only include gauges (attributes with units) in softpoll
                        long timeNow = new Date().time / 1000
                        def metricValues = handleAttribute(attr, d.currentState(attr)?.value, unit)
                        if (metricValues != null) {
                            metrics.addAll(metricValues.collect { metric ->
                                [
                                    name: metric.name,
                                    value: metric.value,
                                    deviceId: d.id,
                                    deviceName: d.displayName,
                                    timestamp: timeNow,
                                    metricType: metric.metricType
                                ]
                            })
                        }
                    }
                }
            }
        }
    }

    if (metrics.size() > 0) {
        sendMetricsToDatadog(metrics)
    }
}

def logSystemProperties() {
    logger('System properties logged', 'info')
}

def sendMetricsToDatadog(metrics) {
    if (prefApiKey == null) {
        logger('Datadog API key not set. Cannot send metrics.', 'error')
        return
    }

    def series = metrics.collect { metric ->
        [
            'metric': "hubitat.${metric.name}",
            'type': metric.metricType,
            'host': 'hubitat',
            'points': [
                [metric.timestamp, metric.value]
            ],
            'tags': ["devicename:${metric.deviceName}", "deviceid:${metric.deviceId}"] + (metric.tags ?: [])
        ]
    }

    def metricsCount = series.size()
    logger("Attempting to send ${metricsCount} metrics to Datadog", 'debug')

    def url = "https://api.datadoghq.com/api/v1/series?api_key=${prefApiKey}"
    def requestBody = [
        'series': series
    ]

    try {
        def postParams = [
            uri: url,
            requestContentType: 'application/json',
            contentType: 'application/json',
            body: groovy.json.JsonOutput.toJson(requestBody)
        ]
        asynchttpPost('handleDatadogResponse', postParams)
    } catch (e) {
        logger("sendMetricsToDatadog(): Request failed: ${e}", 'error')
    }
}

def handleDatadogResponse(response, data) {
    if (response.status != 202) {
        logger("handleDatadogResponse(): Request failed with status ${response.status}", 'error')
    } else {
        logger('handleDatadogResponse(): Metric sent successfully', 'debug')
    }
}

def manageSchedules() {
    logger('manageSchedules()', 'trace')

    Random rand = new Random(now())
    def randomOffset = 0

    try {
        unschedule(softPoll)
    } catch (e) {
        logger('manageSchedules(): Unschedule failed', 'error')
    }

    randomOffset = rand.nextInt(50)
    if (state.softPollingInterval > 0) {
        logger("manageSchedules(): Scheduling softpoll every ${state.softPollingInterval} min (offset ${randomOffset} sec)", 'info')
        schedule("${randomOffset} 0/${state.softPollingInterval} * * * ?", 'softPoll')
    }

}

def manageSubscriptions() {
    logger('manageSubscriptions()', 'trace')

    unsubscribe()

    if (prefLogModeEvents) subscribe(location, 'mode', handleModeEvent)

    if (!accessAllAttributes) {
        def devs
        state.deviceAttributes.each { da ->
            devs = settings."${da.devices}"
            if (devs && (da.attributes)) {
                da.attributes.each { attr ->
                    logger("manageSubscriptions(): Subscribing to attribute: ${attr}, for devices: ${da.devices}", 'info')
                    subscribe(devs, attr, handleEvent, ['filterEvents': filterEvents])
                }
            }
        }
    } else {
        state.selectedAttr.each { entry ->
            d = getDeviceObj(entry.key)
            entry.value.each { attr ->
                logger("manageSubscriptions(): Subscribing to attribute: ${attr}, for device: ${d}", 'info')
                subscribe(d, attr, handleEvent, ['filterEvents': filterEvents])
            }
        }
    }
}

def logger(String message, String level) {
    int loggingLevel = state?.loggingLevelIDE?.toInteger()

    switch (level) {
        case 'error':
            if (loggingLevel >= 1) log.error message
            break
        case 'warn':
        case 'warning':
            if (loggingLevel >= 2) log.warn message
            break
        case 'info':
            if (loggingLevel >= 3) log.info message
            break
        case 'debug':
            if (loggingLevel >= 4) log.debug message
            break
        case 'trace':
            if (loggingLevel >= 5) log.trace message
            break
        default:
            log.warn "Unknown log level: $level. Message: $message"
    }
}


def getEventDetails(evt) {
    return [
        "displayed": evt.displayed,
        "source": evt.source,
        "isStateChange": evt.isStateChange,
        "name": evt.name,
        "value": evt.value,
        "unit": evt.unit,
        "type": evt.type,
        "locationId": evt.locationId,
        "installedAppId": evt.installedAppId,
        "isPhysical": evt.isPhysical(),
        "isDigital": evt.isDigital(),
        "deviceId": evt.getDeviceId(),
        "floatValue": evt.getFloatValue(),
        "integerValue": evt.getIntegerValue(),
        "numberValue": evt.getNumberValue(),
    ]
}

def sendLogToDatadog(evt) {
    if (prefApiKey == null) {
        logger('Datadog API key not set. Cannot send logs.', 'error')
        return
    }

    def eventDetails = getEventDetails(evt)
    def deviceId = evt.getDeviceId()
    def displayName = evt.getDisplayName()
    
    def logEntry = [
        "ddsource": "hubitat",
        "ddservice": "hubitat",
        "type": "event",
        "hostname": "hubitat",
        "device_id": deviceId,
        "device_name": displayName,
        "eventDetails": eventDetails,
        "message": evt.descriptionText
    ]
    
    def url = "https://http-intake.logs.datadoghq.com/v1/input/${prefApiKey}"
    def requestBody = groovy.json.JsonOutput.toJson(logEntry)

    try {
        def postParams = [
            uri: url,
            requestContentType: 'application/json',
            contentType: 'application/json',
            body: requestBody
        ]
        asynchttpPost('handleDatadogLogResponse', postParams)
    } catch (e) {
        logger("sendLogToDatadog(): Request failed: ${e}", 'error')
    }
}

def handleDatadogLogResponse(response, data) {
    if (response.status != 200) {
        logger("handleDatadogLogResponse(): Request failed with status ${response.status}", 'error')
    } else {
        logger('handleDatadogLogResponse(): Log sent successfully', 'debug')

    }
}