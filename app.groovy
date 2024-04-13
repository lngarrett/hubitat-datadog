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
 *   Modifcation History
 *   Date       Name            Change
 *   2024-04-12 Logan Garrett   Initital release
 *****************************************************************************************************************/

definition(
    name: 'Datadog Logger',
    namespace: 'whiskee',
    author: 'Logan Garrett',
    description: 'Log device states to Datadog',
    category: 'Utiliy',
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
                input 'buttons', 'capability.button', title: 'Buttons', multiple: true, required: false
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
    state.loggingLevelIDE = 5
    state.metricQueue = []
    updated()
    log.info "${app.label}: Installed with settings: ${settings}"
}

def uninstalled() {
    log.info "${app.label}: uninstalled"
}

def updated() {
    logger('updated()', 'trace')

    app.updateLabel(appName)
    state.loggingLevelIDE = (settings.configLoggingLevelIDE) ? settings.configLoggingLevelIDE.toInteger() : 3

    state.deviceAttributes = []
    state.deviceAttributes << [ devices: 'accelerometers', attributes: ['acceleration']]
    state.deviceAttributes << [ devices: 'alarms', attributes: ['alarm']]
    state.deviceAttributes << [ devices: 'batteries', attributes: ['battery']]
    state.deviceAttributes << [ devices: 'beacons', attributes: ['presence']]
    state.deviceAttributes << [ devices: 'buttons', attributes: ['button']]
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
def handleAttribute(attr, value, deviceType) {
    def metrics = []


    switch (attr) {
        case 'acceleration':
            metrics << [name: attr, value: binaryValue(value, 'active'), metricType: 'gauge']
            break
        case 'alarm':
            metrics << [name: attr, value: binaryValue(value, 'off', reverse = true), metricType: 'gauge']
            break
        case 'button':
            metrics << [name: attr, value: binaryValue(value, 'pushed'), metricType: 'gauge']
            break
        case 'carbonMonoxide':
            metrics << [name: attr, value: binaryValue(value, 'detected'), metricType: 'gauge']
            break
        case 'consumableStatus':
            metrics << [name: attr, value: binaryValue(value, 'good'), metricType: 'gauge']
            break
        case 'contact':
            if (value == 'open') {
                metrics << [name: attr, value: binaryValue(value, 'open'), metricType: 'count']
            }
            break
        case 'door':
            metrics << [name: attr, value: binaryValue(value, 'opened'), metricType: 'gauge']
            break
        case 'lock':
            metrics << [name: attr, value: binaryValue(value, 'unlocked'), metricType: 'gauge']
            break
        case 'motion':
            if (value == 'active') {
                metrics << [name: attr, value: binaryValue(value, 'active'), metricType: 'count']
            }
            break
        case 'mute':
            metrics << [name: attr, value: binaryValue(value, 'muted'), metricType: 'gauge']
            break
        case 'presence':
            metrics << [name: attr, value: binaryValue(value, 'present'), metricType: 'gauge']
            break
        case 'shock':
            metrics << [name: attr, value: binaryValue(value, 'detected'), metricType: 'gauge']
            break
        case 'sleeping':
            metrics << [name: attr, value: binaryValue(value, 'sleeping'), metricType: 'gauge']
            break
        case 'smoke':
            metrics << [name: attr, value: binaryValue(value, 'detected'), metricType: 'gauge']
            break
        case 'sound':
            metrics << [name: attr, value: binaryValue(value, 'detected'), metricType: 'gauge']
            break
        case 'switch':
            metrics << [name: attr, value: binaryValue(value, 'on'), metricType: 'gauge']
            break
        case 'tamper':
            metrics << [name: attr, value: binaryValue(value, 'detected'), metricType: 'gauge']
            break
        case 'thermostatMode':
            metrics << [name: attr, value: binaryValue(value, 'off', reverse = true), metricType: 'gauge']
            break
        case 'thermostatFanMode':
            metrics << [name: attr, value: binaryValue(value, 'off', reverse = true), metricType: 'gauge']
            break
        case 'thermostatOperatingState':
            metrics << [name: attr, value: binaryValue(value, 'heating'), metricType: 'gauge']
            break
        case 'thermostatSetpointMode':
            metrics << [name: attr, value: binaryValue(value, 'followSchedule', reverse = true), metricType: 'gauge']
            break
        case 'threeAxis':
            def valueXYZ = value.split(',')
            metrics << [name: "${attr}.x", value: valueXYZ[0].toInteger(), metricType: 'gauge']
            metrics << [name: "${attr}.y", value: valueXYZ[1].toInteger(), metricType: 'gauge']
            metrics << [name: "${attr}.z", value: valueXYZ[2].toInteger(), metricType: 'gauge']
            break
        case 'touch':
            metrics << [name: attr, value: binaryValue(value, 'touched'), metricType: 'gauge']
            break
        case 'optimisation':
            metrics << [name: attr, value: binaryValue(value, 'active'), metricType: 'gauge']
            break
        case 'windowFunction':
            metrics << [name: attr, value: binaryValue(value, 'active'), metricType: 'gauge']
            break
        case 'water':
            metrics << [name: attr, value: binaryValue(value, 'wet'), metricType: 'gauge']
            break
        case 'valve':
            metrics << [name: attr, value: binaryValue(value, 'open'), metricType: 'gauge']
            break
        case 'windowShade':
            metrics << [name: attr, value: binaryValue(value, 'closed'), metricType: 'gauge']
            break
        default:
            if (value ==~ /.*[^0-9\.,-].*/) {
                logger("handleAttribute(): String value not explicitly handled: Attribute: ${attr}, Value: ${value}", 'warn')
                return null
            } else {
                try {
                    metrics << [name: attr, value: Float.parseFloat(value), metricType: 'gauge']
                } catch (e) {
                    logger("handleAttribute(): Cannot convert ${value} to float. Skipping.", 'warn')
                    return null
                }
            }
    }

    return metrics
}


def handleEvent(evt) {
    logger("handleEvent(): $evt.displayName ($evt.name) $evt.value", 'info')

    def deviceName = evt.displayName
    def deviceId = evt.deviceId
    def deviceType = evt.name
    long timestamp = evt?.unixTime / 1000

    def metrics = handleAttribute(evt.name, evt.value, deviceType)
    if (metrics != null) {
        sendMetricsToDatadog(metrics.collect { metric ->
            [
                name: metric.name,
                value: metric.value,
                timestamp: timestamp,
                deviceName: deviceName,
                deviceId: deviceId
            ]
        })
    }
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
                            long timeNow = new Date().time / 1000
                            def metricValues = handleAttribute(attr, d.currentState(attr)?.value, d.name)
                            if (metricValues != null) {
                                metrics.addAll(metricValues.collect { metric ->
                                    [
                                        name: metric.name,
                                        value: metric.value,
                                        unit: d.currentState(attr)?.unit,
                                        deviceId: d.id,
                                        deviceName: d.displayName,
                                        timestamp: timeNow
                                    ]
                                })
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
                    long timeNow = new Date().time / 1000
                    def metricValues = handleAttribute(attr, d.currentState(attr)?.value, d.name)
                    if (metricValues != null) {
                        metrics.addAll(metricValues.collect { metric ->
                            [
                                name: metric.name,
                                value: metric.value,
                                unit: d.currentState(attr)?.unit,
                                deviceId: d.id,
                                deviceName: d.displayName,
                                timestamp: timeNow
                            ]
                        })
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
            'type': 'gauge',
            'host': 'hubitat',
            'points': [
                [metric.timestamp, metric.value]
            ],
            'tags': ["devicename:${metric.deviceName}", "deviceid:${metric.deviceId}"]
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

private manageSchedules() {
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

private manageSubscriptions() {
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

private logger(msg, level = 'debug') {
    if (level == 'error') {
        log.error msg
    } else if (level == 'warn') {
        log.warn msg
    } else if (level == 'info') {
        log.info msg
    } else if (level == 'trace') {
        log.trace msg
    } else {
        log.debug msg
    }
}