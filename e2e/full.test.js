import jestExpect from 'expect'
import { web, element, by, expect, waitFor, device } from 'detox'

const app = {
  _timeout: 5000, //ms
  loadMessagesButton: element(by.text(/load messages/i)),
  loadGDPRPMButton: element(by.text(/load gdpr pm/i)),
  loadCCPAPMButton: element(by.text(/load ccpa pm/i)),
  clearDataButton: element(by.text(/clear all/i)),
  sdkElement: element(by.id('sdkStatus')),
  presentingStatusText: 'Presenting',
  finishedStatusText: 'Finished',
  messageTitle: web.element(by.web.cssSelector('#notice > p')),
  acceptAllButton: web.element(by.web.cssSelector('.sp_choice_type_11')),
  rejectAllButton: web.element(by.web.cssSelector('.sp_choice_type_13')),
  pmCancelButton: web.element(by.web.cssSelector('.sp_choice_type_CANCEL')),
  pmToggleOn: web.element(by.web.cssSelector('button[aria-checked=true]')),
  pmToggleOff: web.element(by.web.cssSelector('button[aria-checked=false]')),

  getGDPRUUID: async function () {
    return (await element(by.id('gdpr.uuid')).getAttributes()).text
  },

  getCCPAUUID: async function () {
    return (await element(by.id('ccpa.uuid')).getAttributes()).text
  },

  forSDKToBePresenting: async function () {
    await waitFor(this.sdkElement)
      .toHaveText(this.presentingStatusText)
      .withTimeout(this._timeout)
  },

  forSDKToBeFinished: async function () {
    await waitFor(this.sdkElement)
      .toHaveText(this.finishedStatusText)
      .withTimeout(this._timeout)
  },

  loadGDPRPM: async function () {
    await this.loadGDPRPMButton.tap()
  },

  loadCCPAPM: async function () {
    await this.loadCCPAPMButton.tap()
  },

  dismissPM: async function () {
    await this.pmCancelButton.tap()
  },

  assertNoMessageShow: async function () {
    await this.reloadMessages({ clearData: false })
    await this.forSDKToBeFinished()
  },

  reloadMessages: async function ({ clearData }) {
    if (clearData) await this.clearDataButton.tap()
    await this.loadMessagesButton.tap()
  },

  acceptAllGDPRMessage: async function (withTitle = 'GDPR Message') {
    await expect(app.messageTitle).toHaveText(withTitle)
    await app.acceptAllButton.tap()
  },

  acceptAllCCPAMessage: async function (withTitle = 'CCPA Message') {
    await expect(app.messageTitle).toHaveText(withTitle)
    await app.acceptAllButton.tap()
  },

  rejectAllGDPRMessage: async function (withTitle = 'GDPR Message') {
    await expect(app.messageTitle).toHaveText(withTitle)
    await app.rejectAllButton.tap()
  },

  rejectAllCCPAMessage: async function (withTitle = 'CCPA Message') {
    await expect(app.messageTitle).toHaveText(withTitle)
    await app.rejectAllButton.tap()
  },
}

const assertUUIDsDidntChange = async () => {
  const gdprUUIDBeforeReloading = app.gdprUUID
  const ccpaUUIDBeforeReloading = app.ccpaUUID
  await app.assertNoMessageShow()
  jestExpect(gdprUUIDBeforeReloading).toEqual(app.gdprUUID)
  jestExpect(ccpaUUIDBeforeReloading).toEqual(app.ccpaUUID)
}

const assertAllPMToggles = async (loadPMFn, { toggled, togglesCount }) => {
  await loadPMFn()
  if (toggled) {
    await expect(app.pmToggleOn.atIndex(togglesCount - 1)).toExist()
  } else {
    await expect(app.pmToggleOff.atIndex(togglesCount - 1)).toExist()
  }
  await app.dismissPM()
}

describe('SourcepointSDK', () => {
  beforeAll(async () => {
    await device.launchApp()
  })

  it('Accepting All, works', async () => {
    await app.forSDKToBePresenting()
    await app.acceptAllGDPRMessage()
    await app.acceptAllCCPAMessage()
    await app.forSDKToBeFinished()
    await assertUUIDsDidntChange()
    await assertAllPMToggles(() => app.loadGDPRPM(), {
      toggled: true,
      togglesCount: 5,
    })
    await assertAllPMToggles(() => app.loadCCPAPM(), {
      toggled: true,
      togglesCount: 3,
    })
  })

  it('Rejecting All, works', async () => {
    await app.reloadMessages({ clearData: true })
    await app.forSDKToBePresenting()
    await app.rejectAllGDPRMessage()
    await app.rejectAllCCPAMessage()
    await app.forSDKToBeFinished()
    await assertUUIDsDidntChange()
    await assertAllPMToggles(() => app.loadGDPRPM(), {
      toggled: false,
      togglesCount: 5,
    })
    await assertAllPMToggles(() => app.loadCCPAPM(), {
      toggled: false,
      togglesCount: 3,
    })
  })
})
