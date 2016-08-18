//
//  SubString.h
//  WordExtractor
//
//  Created by TJT on 8/18/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SubString : NSString

@property (nonatomic, assign, readonly) NSUInteger          start;
@property (nonatomic, assign, readonly) NSUInteger          end;
@property (nonatomic, assign, readonly) NSUInteger          length;

- (void)updateWithString:(NSString *)str start:(NSInteger)start end:(NSInteger)end;

@end
