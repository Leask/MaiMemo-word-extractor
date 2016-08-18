//
//  StringWrapperTest.m
//  WordExtractor
//
//  Created by TJT on 8/18/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "StringWrapper.h"

@interface StringWrapperTest : XCTestCase

@end

@implementation StringWrapperTest

- (void)setUp {
    [super setUp];
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

- (void)testExample {
    // This is an example of a functional test case.
    // Use XCTAssert and related functions to verify your tests produce the correct results.
}

- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
    }];
}

- (void)testStringWrapper {
    StringWrapper *wrapper = [StringWrapper stringWithString:@"Hello"];
    StringWrapper *test = [StringWrapper stringWithString:@"hello"];
    NSSet *set = [[NSSet alloc] initWithObjects:wrapper, nil];
    if (![set containsObject:test]) {
        [NSException raise:@"TestFailure" format:@""];
    }
}

- (void)testHash {
    StringWrapper *str = [StringWrapper stringWithString:@"Hello"];
    StringWrapper *containsTest = [StringWrapper stringWithString:@"hello"];
    NSMapTable *table = [[NSMapTable alloc] initWithKeyOptions:NSPointerFunctionsStrongMemory valueOptions:NSPointerFunctionsStrongMemory capacity:2];
    [table setObject:@"1" forKey:str];
    NSLog(@">>> %lu %@, %lu %@", str.hash, str, containsTest.hash, containsTest);
    NSSet *set = [[NSSet alloc] initWithObjects:str, nil];
    if ([table objectForKey:containsTest] == nil) {
        [NSException raise:@"TestFailure" format:@""];
    }
    if (![set containsObject:containsTest]) {
        [NSException raise:@"TestFailure" format:@""];
    }
    if (![set containsObject:@"hello"]) {
        [NSException raise:@"TestFailure" format:@""];
    }
    if ([set containsObject:@YES]) {
        [NSException raise:@"TestFailure" format:@""];
    }
}

@end
