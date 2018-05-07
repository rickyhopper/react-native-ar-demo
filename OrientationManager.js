'use strict';
/**
 * This exposes the native OrientationManager module as a JS module. This allows
 * the react-native app to utilize the orientation of the phone.
 */
import { NativeModules } from 'react-native';
module.exports = NativeModules.OrientationManager;
