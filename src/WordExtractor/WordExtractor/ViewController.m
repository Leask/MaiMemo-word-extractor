//
//  ViewController.m
//  WordExtractor
//
//  Created by TJT on 8/17/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import "ViewController.h"
#import "SubString.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    SubString *subStr = [SubString new];
    [subStr updateWithString:@"hello world" start:0 end:6];
    SubString *subStr1 = [SubString new];
    [subStr1 updateWithString:@"hello johy" start:0 end:6];
    NSMutableSet *set = [NSMutableSet new];
    [set addObject:subStr];
    if ([set containsObject:subStr1]) {
        NSLog(@"%@", @"contains");
    } else {
        NSLog(@"%@, %lu, %lu", @"not contains", subStr.hash, subStr1.hash);
    }
    // Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];

    // Dispose of any resources that can be recreated.
}

@end
