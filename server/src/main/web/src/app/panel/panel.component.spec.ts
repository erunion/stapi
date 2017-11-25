import { async, fakeAsync, ComponentFixture, TestBed, tick } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { RestApiService } from '../rest-api/rest-api.service';
import { PanelApi } from './panel-api.service';
import { WindowReferenceService } from '../window-reference/window-reference.service';

import { PanelComponent } from './panel.component';
import { PanelApiKeysApi } from './api-keys/panel-api-keys-api.service';

class PanelApiMock {
	public getMe() {}
	public getOAuthAuthorizeUrl() {}
}

class PanelApiKeysApiMock {
	public getApiKeys() {
		return Promise.resolve({});
	}
}

class WindowReferenceServiceMock {
	public getWindow() {}
}

class RestApiServiceMock {
	public getApi() {}
}

describe('PanelComponent', () => {
	const NAME = 'NAME';
	const EMAIL = 'EMAIL';
	const URL = 'URL';
	let component: PanelComponent;
	let fixture: ComponentFixture<PanelComponent>;
	let panelApiMock: PanelApiMock;
	let panelApiKeysApiMock: PanelApiKeysApiMock;
	let windowReferenceServiceMock: WindowReferenceServiceMock;
	let restApiServiceMock: RestApiServiceMock;
	let api: any;

	beforeEach(async(() => {
		panelApiMock = new PanelApiMock();
		panelApiKeysApiMock = new PanelApiKeysApiMock();
		windowReferenceServiceMock = new WindowReferenceServiceMock();
		restApiServiceMock = new RestApiServiceMock();

		api = {
			on: jasmine.createSpy('on')
		};
		spyOn(restApiServiceMock, 'getApi').and.returnValue(api);

		TestBed.configureTestingModule({
			declarations: [
				PanelComponent
			],
			schemas: [NO_ERRORS_SCHEMA],
			providers: [
				{
					provide: PanelApiKeysApi,
					useValue: panelApiKeysApiMock
				},
				{
					provide: PanelApi,
					useValue: panelApiMock
				},
				{
					provide: WindowReferenceService,
					useValue: windowReferenceServiceMock
				},
				{
					provide: RestApiService,
					useValue: restApiServiceMock
				}
			]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(PanelComponent);
		component = fixture.componentInstance;
	});

	describe('initialization without error', () => {
		let permissions;
		var resolve;

		beforeEach(() => {
			permissions = [];
			spyOn(panelApiMock, 'getMe').and.returnValue(new Promise((_resolve) => {
				resolve = () => {
					_resolve({
						name: NAME,
						email: EMAIL,
						permissions: permissions
					});
				}
			}));
		});

		it('is performed', fakeAsync(() => {
			resolve();
			fixture.detectChanges();

			expect(component).toBeTruthy();

			fixture.whenStable().then(() => {
				expect(component.getName()).toEqual(NAME);
			});
		}));

		it('allows tab switching', () => {
			resolve();
			fixture.detectChanges();

			expect(component.apiKeysIsVisible()).toBeTrue();
			expect(component.accountSettingsIsVisible()).toBeFalse();
			expect(component.adminManagementIsVisible()).toBeFalse();

			fixture.whenStable().then(() => {
				fixture.detectChanges();

				fixture.debugElement.query(By.css('.account-panel__section-selector:nth-child(2)')).nativeElement.click();

				expect(component.apiKeysIsVisible()).toBeFalse();
				expect(component.accountSettingsIsVisible()).toBeTrue();
				expect(component.adminManagementIsVisible()).toBeFalse();
				expect(fixture.debugElement.query(By.css('.account-panel__section-selector:nth-child(3)'))).toBeNull();
			});
		});

		describe('admin management', () => {
			beforeEach(() => {
				permissions = ['API_KEY_MANAGEMENT', 'ADMIN_MANAGEMENT']
			});

			it('allows tab switching', () => {
				resolve();
				fixture.detectChanges();

				expect(component.apiKeysIsVisible()).toBeTrue();
				expect(component.accountSettingsIsVisible()).toBeFalse();
				expect(component.adminManagementIsVisible()).toBeFalse();

				fixture.whenStable().then(() => {
					fixture.detectChanges();

					fixture.debugElement.query(By.css('.account-panel__section-selector:nth-child(3)')).nativeElement.click();

					expect(component.apiKeysIsVisible()).toBeFalse();
					expect(component.accountSettingsIsVisible()).toBeFalse();
					expect(component.adminManagementIsVisible()).toBeTrue();
				});
			});
		});
	});

	describe('initialization with error', () => {
		let window = {
			location: {
				href: ''
			}
		};
		let error = {
			status: 403
		};

		beforeEach(() => {
			spyOn(panelApiMock, 'getMe').and.returnValue(new Promise((resolve, reject) => {
				reject(error);
			}));
			spyOn(panelApiMock, 'getOAuthAuthorizeUrl').and.returnValue(new Promise((resolve) => {
				resolve({
					url: URL
				});
			}));
			spyOn(windowReferenceServiceMock, 'getWindow').and.returnValue(window);

			fixture.detectChanges();
		});

		it('is performed', () => {
			expect(component).toBeTruthy();
			expect(component.isAuthenticated()).toBeFalse();
			expect(component.isAuthenticationRequired()).toBeFalse();
			expect(component.isRedirecting()).toBeFalse();
			expect(component.getButtonLabel()).toBe('Authenticate with GitHub');

			api.on.calls.argsFor(0)[1](error);

			fixture.detectChanges();

			fixture.whenStable().then(() => {
				expect(component.isAuthenticated()).toBeFalse();
				expect(component.isAuthenticationRequired()).toBeTrue();
				expect(component.isRedirecting()).toBeFalse();
				expect(component.getButtonLabel()).toBe('Authenticate with GitHub');
				fixture.detectChanges();

				fixture.debugElement.query(By.css('.panel-github-authenticate')).nativeElement.click();

				fixture.whenStable().then(() => {
					expect(component.getName()).toBe('stranger');
					expect(window.location.href).toBe(URL);
					expect(component.isAuthenticated()).toBeFalse();
					expect(component.isAuthenticationRequired()).toBeTrue();
					expect(component.isRedirecting()).toBeTrue();
					expect(component.getButtonLabel()).toBe('Redirecting to GitHub...');
				});
			});
		});
	});
});
